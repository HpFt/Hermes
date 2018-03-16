#!/usr/bin/env bash

declare -A fun

fun+=(
    ["enableDocker"]="Enable docker service"
    ["removeDocker"]="Remove docker package"
    ["removeDockerCompose"]="Remove docker-compose"
    ["removeServices"]="Remove services"
    ["installDocker"]="Install docker package"
    ["installDockerCompose"]="Install docker-compose"
    ["checkDocker"]="Check docker service"
    ["checkDockerCompose"]="Check docker-compose"
    ["downloadConfig"]="Download docker-compose.yml"
    ["createConfig"]="Create hermes.properties template"
    ["startPostgres"]="Start postgres service"
    ["preparePostgres"]="Prepare postgres"
    ["createDatabase"]="Create database"
    ["startHermes"]="Start Hermes"
    ["stopHermes"]="Stop Hermes"
    ["logsHermes"]="Attach logs Hermes"
    ["pullHermes"]="Update Hermes"
)


function wrap {
    local command
    local name
    command=$1
    name=${fun["$command"]}
    [[ -z $name ]] && echo "No such function description for $1"  && exit 1
    cprint "$name"
    eval "$*"
    cprint "DONE: $name"
}

function cprint {
    printf "=== \033[0;37m$1\033[0m === \n"
}

function enableDocker {
    sudo systemctl enable docker
}

function removeDocker {
    sudo apt-get remove -y docker docker-engine docker-ce docker.io
}

function removeDockerCompose {
    sudo rm -rf /usr/local/bin/docker-compose
}

function removeServices {
    sudo docker-compose stop
    sudo docker-compose down
    sudo docker system prune -af
    ls -1 | grep -v `basename $0` | xargs sudo rm -rf
}

function installDocker {
    wrap removeDocker
    sudo apt-get install -y \
        apt-transport-https \
        ca-certificates \
        curl \
        software-properties-common
    curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
    sudo add-apt-repository -y \
        "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
        $(lsb_release -cs) \
        stable"
    sudo apt-get update -y
    sudo apt-get install -y docker-ce
}

function installDockerCompose {
    rm -rf /usr/local/bin/docker-compose
    sudo curl -L https://github.com/docker/compose/releases/download/1.19.0/docker-compose-`uname -s`-`uname -m` -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
}
function checkDocker {
    service=`systemctl is-enabled docker 2>&1`

    case "$service" in
        enabled);;
        disabled)
            wrap enableDocker
        ;;
        *)
            wrap installDocker
            wrap enableDocker
        ;;
    esac
}

function checkDockerCompose {
    compose=`whereis docker-compose | awk '{print $2}'`
    if [[ ! -x "${compose}" ]]; then
        wrap installDockerCompose
    fi
}

function downloadConfig {
    curl -L https://raw.githubusercontent.com/pTykvin/Hermes/master/build-scripts/docker-compose.yml -o docker-compose.yml
}

function createConfig {
    if [[ ! -x hermes.properties ]]; then
        echo \# host=http://hermes.hermitage.ru/  > hermes.properties
        echo \# life_time=PT10M >> hermes.properties
    fi
}

function createDatabase {
    sudo docker-compose exec postgres psql -U postgres -c "CREATE DATABASE hermes;" 2>/dev/null
    if [[ ! "$?" -eq 0 ]]; then
        echo "Repeat $1"
        sleep 2
        if [[ $1 -lt 3 ]]; then
            createDatabase $(($1+1))
        else
            cprint "Can't create database"
            exit -1
        fi
    fi
}

function startPostgres {
    sudo docker-compose up -d postgres
}

function preparePostgres {
    postgres=`sudo docker-compose ps postgres 2>&1 | grep -E "No such service|Exit"`
    if [[ ! -z postgres ]]; then
        wrap startPostgres
    fi
    sleep 2
    wrap createDatabase
}

function startHermes {
    sudo docker-compose up -d hermes
}

function stopHermes {
    sudo docker-compose stop hermes
}

function pullHermes {
    sudo docker-compose pull hermes
}

function logsHermes {
    sudo docker-compose logs -f hermes
}

sudo echo

case "$1" in
    setup)
        wrap checkDocker
        wrap checkDockerCompose
        wrap downloadConfig
        wrap createConfig
        wrap preparePostgres
    ;;
    environment)
        wrap checkDocker
        wrap checkDockerCompose
    ;;
    database)
        wrap downloadConfig
        wrap createConfig
        wrap preparePostgres
    ;;
    configure)
        wrap createConfig
    ;;
    start)
        wrap startHermes
    ;;
    stop)
        wrap stopHermes
    ;;
    restart)
        wrap stopHermes
        wrap startHermes
    ;;
    update)
        wrap pullHermes
        wrap stopHermes
        wrap startHermes
    ;;
    logs)
        wrap logsHermes
    ;;
    undeploy)
        # Don't call this - remove all
        wrap removeServices
        wrap removeDockerCompose
        wrap removeDocker
    ;;
    *)
        echo "Command $1 not supported"
        echo "Usage $0 setup|environment|database|configure|start|stop|restart|update|logs"
    ;;
esac
