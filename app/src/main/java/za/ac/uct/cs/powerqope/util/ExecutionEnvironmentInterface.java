 /* 
 PersonalHttpProxy 1.5
 Copyright (C) 2013-2015 Ingo Zenz

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.

 Find the latest version at http://www.zenz-solutions.de/personalhttpproxy
 Contact:i.z@gmx.net 
 */

package za.ac.uct.cs.powerqope.util;

 import java.io.IOException;
 import java.io.InputStream;

 public interface ExecutionEnvironmentInterface {

	 public int getEnvironmentID();
	 public String getEnvironmentVersion();
	 public void wakeLock();
	 public void releaseWakeLock();
	 public void releaseAllWakeLocks();
	 public String getWorkDir();
	 public void onReload()  throws IOException;
	 public InputStream getAsset(String path) throws IOException;
	 public boolean hasNetwork();
	 public boolean protectSocket(Object socket, int type);
	 public void migrateConfig() throws IOException;
}