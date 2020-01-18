#!/bin/bash
#populate mysql database with test entries
sudo mysql -e "DELETE FROM db_apache.t_user;"
sudo mysql -e "DELETE FROM db_apache.t_score;"
