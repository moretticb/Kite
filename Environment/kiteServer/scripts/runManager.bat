REM
REM
REM       runManager.bat - Kite Messenger - Threaded chat
REM  Copyright (c) 2012 Caio Benatti Moretti <caiodba@gmail.com>
REM                 http://www.moretticb.com/Kite
REM
REM  Last update: 9 October 2018
REM
REM  This is free software: you can redistribute it and/or modify
REM  it under the terms of the GNU General Public License as published by
REM  the Free Software Foundation, either version 3 of the License, or
REM  (at your option) any later version.
REM
REM  This is distributed in the hope that it will be useful,
REM  but WITHOUT ANY WARRANTY; without even the implied warranty of
REM  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
REM  GNU General Public License for more details.
REM
REM  You should have received a copy of the GNU General Public License
REM  along with this program. If not, see <http://www.gnu.org/licenses/>.
REM
REM


cd ..\data
@java -classpath ..\jars\hsqldb.jar org.hsqldb.util.DatabaseManager %1 %2 %3 %4 %5 %6 %7 %8 %9
