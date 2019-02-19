#!/usr/bin/env sh
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
    printf "JAVA_HOME is not set before running. Please set JAVA first.\n"
    exit
  fi

  if [ ! -n "$1" ]; then
    printf "Version not set. Please set version first.\n"
    exit
  fi


  printf "${BLUE}Downloading Clusterize...${NORMAL}\n"

  if ! command -v curl > /dev/null 2>&1; then
    printf "Error: Curl not installed.\n"
    exit
  fi

  BASE_DIR="$HOME/.clusterize"
  SOURCE_DIR="$HOME/.clusterize/clusterize"


  curl -fsSL https://github.com/jakub-tucek/metacentrum-cli/releases/download/$1/clusterize.tar --output $BASE_DIR/clusterize-bin.tar || {
    echo "Error: Unable to download version $1\n"
    exit 1
  }


  printf "${BLUE}Preparing folder...${NORMAL}\n"


  mkdir -p $BASE_DIR > /dev/null 2>&1 || {
    echo "Error: Unable to create install folder\n"
    exit 1
  }

  rm -Rf $SOURCE_DIR || {
    echo "Error: Unable to delete existing installation\n"
    exit 1
  }

  tar -xf $BASE_DIR/clusterize-bin.tar -C $BASE_DIR || {
    echo "Error: Unable to untar\n"
    exit 1
  }

  printf "${BLUE}Installing ...${NORMAL}\n"


  echo '#/bin/bash' > "$SOURCE_DIR/clusterize" || {
    echo "Error: Unable create init script (1)\n"
    exit 1
  }
  echo "export JAVA_HOME=$JAVA_HOME" >> "$SOURCE_DIR/clusterize" || {
    echo "Error: Unable create init script (2)\n"
    exit 1
  }
  echo "export PBSPRO_IGNORE_KERBEROS=yes" >> "$SOURCE_DIR/clusterize" || {
    echo "Error: Unable create init script (3)\n"
    exit 1
  }

  echo "$SOURCE_DIR/bin/clusterize \$\@" >> "$SOURCE_DIR/clusterize" || {
    echo "Error: Unable create init script (4)\n"
    exit 1
  }
  chmod 777 $SOURCE_DIR/clusterize || {
    echo "Error: Unable create init script (5)\n"
    exit 1
  }
  chmod +x $SOURCE_DIR/clusterize || {
    echo "Error: Unable create init script (6)\n"
    exit 1
  }

  printf "${BLUE}Cleaning up ...${NORMAL}\n"

  rm -Rf $BASE_DIR/clusterize-bin.tar

  printf "${GREEN}"
  echo '       _____ _     _    _  _____ _______ ______ _____  _____ ____________'
  echo '    / ____| |   | |  | |/ ____|__   __|  ____|  __ \|_   _|___  /  ____| '
  echo '   | |    | |   | |  | | (___    | |  | |__  | |__) | | |    / /| |__    '
  echo '   | |    | |   | |  | |\___ \   | |  |  __| |  _  /  | |   / / |  __|   '
  echo '   | |____| |___| |__| |____) |  | |  | |____| | \ \ _| |_ / /__| |____  '
  echo '    \_____|______\____/|_____/   |_|  |______|_|  \_\_____/_____|______| '
  echo '                                                    ....is now installed!'
  echo ''
  printf "${NORMAL}\n"

  printf "Add following line to your .bashrc to use clusterize from command line:\n\n"
  printf "export PATH=\$PATH:$SOURCE_DIR\n\n"
}

main $1