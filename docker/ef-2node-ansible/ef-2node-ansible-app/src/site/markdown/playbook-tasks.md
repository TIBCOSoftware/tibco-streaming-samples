# Ansible playbook tasks

Below we have selected ansible playbook tasks with brief description. 

Variables like `{{ projectId }}` and `{{ projectId_ver }}` and others are defined in pom.xml and passed to ansible plugin during maven execution. 


* Task with module [copy](https://docs.ansible.com/ansible/latest/modules/copy_module.html), you need to define source folder and destination folder.

``` 
- name: Copy start-node script to base image work directory
  copy:
    src:  "{{ project_basedir }}/target/dependencies/{{ platform }}.zip"
    dest: "{{ project_build_directory }}/docker/base/maven/"
```

* Task with module [replace](https://docs.ansible.com/ansible/latest/modules/replace_module.html) and loop. This is ansible recommended method to customize config/settings/text files during the playbook execution. Removing a string from file in specific path is also available in this module.
Search is based on defined regular expressions in the list below which is executed in a loop.
  
```
- name: Update dockerfile before building base image
  replace:
    path: "{{ project_build_directory }}/docker/base/Dockerfile"
    regexp: "{{ item.search }}"
    replace: "{{ item.replace }}"
  with_items:
    - { search: '(^LABEL build-image=)(.*)$', replace: "LABEL build-image={{ projectId_ver }}" }
    - { search: '(^###Note:\s)(.*)$', replace: '###Note: LABEL statement build by Ansible playbook' }
```

* Task with module [docker_image](https://docs.ansible.com/ansible/latest/modules/docker_image_module.html) to build an image. 

```
- name: Building SB base image - sbrt-base
  docker_image:
    build:
      path: "{{ project_build_directory }}/docker/base/"
      pull: no
    push: no
    name: sbrt-base
    repository: sbrt-base
    tag: "{{ sbrt_ver }}"
    source: build
```

* Task with [docker_container](https://docs.ansible.com/ansible/latest/modules/docker_container_module.html) module to start a container based on a specific image name. 

```
- name: Start container A.{{ projectId }}
  docker_container:
    name: A.{{ projectId }}
    image: docker/{{ projectId }}:{{ projectId_ver }}
    hostname: A.example.com
    networks:
      - name: example.com
        aliases:
          - A.example.com
    env:
      STREAMING_NODENAME: A.{{ projectId }}
    state: started
```

* Task with [shell](https://docs.ansible.com/ansible/latest/modules/shell_module.html) module can execute shell commands on targets. In the task below we execute docker exec … command and register the result in NodeAresults variable. After this is completed successfully, the next task displays the results on screen based on module debug.

```
- name: Run epadmin command on Node A
  shell: docker exec A.{{ projectId }} epadmin --servicename={{ projectId }} display cluster
  register: NodeAresults
  when: skipTests == 'false'
- name: Node A
  debug: var=NodeAresults.stdout_lines
```

* Task with [docker_container](https://docs.ansible.com/ansible/latest/modules/docker_container_module.html) module to stop and remove container A

```
- name: Stop and remove container A
  docker_container:
    name: A.{{ projectId }}
    state: absent
```
