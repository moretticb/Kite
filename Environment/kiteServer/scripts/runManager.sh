#!/bin/bash

##############################################################################
##
##        runManager.sh - Kite Messenger - Threaded chat
##  Copyright (c) 2012 Caio Benatti Moretti <caiodba@gmail.com>
##                 http://www.moretticb.com/Kite
##
##  Last update: 9 October 2018
##
##  This is free software: you can redistribute it and/or modify
##  it under the terms of the GNU General Public License as published by
##  the Free Software Foundation, either version 3 of the License, or
##  (at your option) any later version.
##
##  This is distributed in the hope that it will be useful,
##  but WITHOUT ANY WARRANTY; without even the implied warranty of
##  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
##  GNU General Public License for more details.
##
##  You should have received a copy of the GNU General Public License
##  along with this program. If not, see <http://www.gnu.org/licenses/>.
##
##############################################################################


cd ../data
java -cp ../jars/hsqldb.jar org.hsqldb.util.DatabaseManager $1 $2 $3 $4 $5 $6 $7 $8 $9
