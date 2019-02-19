main() {
  # Use colors, but only if connected to a terminal, and that terminal
  # supports them.
  if which tput >/dev/null 2>&1; then
      ncolors=$(tput colors)
  fi
  if [ -t 1 ] && [ -n "$ncolors" ] && [ "$ncolors" -ge 8 ]; then
    RED="$(tput setaf 1)"
    GREEN="$(tput setaf 2)"
    YELLOW="$(tput setaf 3)"
    BLUE="$(tput setaf 4)"
    BOLD="$(tput bold)"
    NORMAL="$(tput sgr0)"
  else
    RED=""
    GREEN=""
    YELLOW=""
    BLUE=""
    BOLD=""
    NORMAL=""
  fi

  # Only enable exit-on-error after the non-critical colorization stuff,
  # which may fail on systems lacking tput or terminfo
  set -e

  if [ ! -n "$JAVA_HOME" ]; then
    printf "JAVA_HOME is not set before running. Please set JAVA first."
    exit
  fi

  if [ ! -n "$1" ]; then
    printf "Version not set. Please set version first."
    exit
  fi


  printf "${BLUE}Downloading Clusterize...${NORMAL}\n"

  if ! command -v curl > /dev/null 2>&1; then
    printf "Error: Curl not installed."
    exit
  fi

  BASE_DIR="~/.clusterize/clusterize"


  command -v curl -fsSL https://github.com/jakub-tucek/metacentrum-cli/archive/$1.tar.gz --output ~/.clusterize/clusterize.tar || {
    echo "Error: Unable to download version $1"
    exit 1
  }

  command -v mkdir -p ~/.clusterize > /dev/null 2>&1 || {
    echo "Error: Unable to create install folder"
    exit 1
  }

  command -v rm -Rf $BASE_DIR >/dev/null 2>&1 || {
    echo "Error: Unable to delete existing installation"
    exit 1
  }

  command -v tar -xf ~/.clusterize/clusterize.tar >/dev/null 2>&1 || {
    echo "Error: Unable to untar"
    exit 1
  }

  command -v echo '#/bin/bash' > $BASE_DIR/clusterize || {
    echo "Error: Unable create init script (1)"
    exit 1
  }
  command -v echo "export JAVA_HOME=$JAVA_HOME" >> $BASE_DIR/clusterize >/dev/null 2>&1 || {
    echo "Error: Unable create init script (2)"
    exit 1
  }
  command -v echo './bin/clusterize' >> $BASE_DIR/clusterize >/dev/null 2>&1 || {
    echo "Error: Unable create init script (3)"
    exit 1
  }
  command -v chmod 777 $BASE_DIR/clusterize >/dev/null 2>&1 || {
    echo "Error: Unable create init script (4)"
    exit 1
  }
  command -v chmod +x $BASE_DIR/clusterize >/dev/null 2>&1 || {
    echo "Error: Unable create init script (5)"
    exit 1
  }

  printf "${BLUE}Installing ...${NORMAL}\n"


  printf "${GREEN}"
  echo '       _____ _     _    _  _____ _______ ______ _____  _____ ____________'
  echo '    / ____| |   | |  | |/ ____|__   __|  ____|  __ \|_   _|___  /  ____| '
  echo '   | |    | |   | |  | | (___    | |  | |__  | |__) | | |    / /| |__    '
  echo '   | |    | |   | |  | |\___ \   | |  |  __| |  _  /  | |   / / |  __|   '
  echo '   | |____| |___| |__| |____) |  | |  | |____| | \ \ _| |_ / /__| |____  '
  echo '    \_____|______\____/|_____/   |_|  |______|_|  \_\_____/_____|______| '
  echo '                                                    ....is now installed!'
  echo ''
  printf "${NORMAL}"

  printf "Add following line to your .bashrc to use clusterize from command line:"
  printf "export clusterize=~/.clusterize/clusterize"
  env zsh -l
}

main $1