/*
	This file is part of RUbioSeq-GUI.

	RUbioSeq-GUI is free software: you can redistribute it and/or modify
	it under the terms of the GNU General Public License as published by
	the Free Software Foundation, either version 3 of the License, or
	(at your option) any later version.

	RUbioSeq-GUI is distributed in the hope that it will be useful,
	but WITHOUT ANY WARRANTY; without even the implied warranty of
	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
	GNU General Public License for more details.

	You should have received a copy of the GNU General Public License
	along with RUbioSeq-GUI.  If not, see <http://www.gnu.org/licenses/>.
*/
package es.uvigo.ei.sing.rubioseq.gui.util;

import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.codec.digest.DigestUtils;

import es.uvigo.ei.sing.rubioseq.DataStore;
import es.uvigo.ei.sing.rubioseq.DataStoreMode;
import es.uvigo.ei.sing.rubioseq.DataStoreType;
import es.uvigo.ei.sing.rubioseq.RUbioSeqConfiguration;
import es.uvigo.ei.sing.rubioseq.User;

/**
 * This class is responsible for the initializacion of the BD. See method 
 * initDatabase() for more information.
 * 
 * @author hlfernandez
 */
public class DBInitializer implements ServletContextListener{
	
	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
	}

	/**
	 * This method is responsible for the initializacion of the BD, that is:
	 * - Creating the default RUbioSeqConfiguration.
	 * - Creating the default users.
	 * - Creating a datastore pointing to "/" for the admin user.
	 * 
	 * This method also plays a key role in the deployment of the application 
	 * since it prints the message "[DBInitializer] DB initialized." which is
	 * triggered by the launch-rubioseq-gui.sh in order to know that the app. is
	 * deployed and launch a browser.
	 * 
	 * @author hlfernandez
	 */
    static void initDatabase()  {
        System.out.println("[DBInitializer] Initializing DB ...");
        
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("rubioseq-database");
        EntityManager em = emf.createEntityManager();				
		EntityTransaction tx = null;
		try{
			/*
			 * Store Global Configuration
			 */
			if (em.createQuery("SELECT u FROM RUbioSeqConfiguration u").getResultList().size() == 0){
				RUbioSeqConfiguration config = new RUbioSeqConfiguration();
				config.setRubioseqCommand("/opt/rubioseq3/rubioseq.pl");
				config.setPrivateDatastoresRootDirectory("/path/to/private/datastores/root");
				config.setCreatePrivateDatastoresOnUserRegistration(false);
				
				tx = em.getTransaction();			
				try{
					tx.begin();		
						em.persist(config);		
					tx.commit();						
				}finally{
					if (tx !=null && tx.isActive()){ tx.rollback(); }
				}
			}
			/*
			 * Create Default Users
			 */
			if (em.createQuery("SELECT u FROM User u").getResultList().size() == 0){
				User user = new User();
				user.setUsername("rubiosequser");
				user.setPassword(DigestUtils.md5Hex("rubioseqpass"));
				user.setAdmin(false);
				user.setEmail("rubiosequser@rubioseg.org");
				
				tx = em.getTransaction();			
				try{
					tx.begin();		
						em.persist(user);		
					tx.commit();						
				}finally{
					if (tx !=null && tx.isActive()){ tx.rollback(); }
				}
				
				user = new User();
				user.setUsername("admin");
				user.setPassword(DigestUtils.md5Hex("admin"));
				user.setAdmin(true);
				user.setEmail("rubiosequser@rubioseg.org");
				
				tx = em.getTransaction();			
				try{
					tx.begin();		
						em.persist(user);		
					tx.commit();						
				}finally{
					if (tx !=null && tx.isActive()){ tx.rollback(); }
				}
			}
			/*
			 * Create Default Datastores
			 */
			boolean createDefaultAdminDatastore = true;
			List<User> adminUsers = getAdminUsers(em);
			@SuppressWarnings("unchecked")
			List<DataStore> datastores = (List<DataStore>) em.createQuery("SELECT d FROM DataStore d").getResultList();
			for(User adminUser : adminUsers){
				if (datastores.size() == 0){
					createDefaultAdminDatastore = true;
				} else{
					for(DataStore d : datastores){
						if(d.getUser()!=null && d.getUser().equals(adminUser) && d.getPath().equals("/")){
							createDefaultAdminDatastore = false;
						}
					}
				}
				if(createDefaultAdminDatastore){
					DataStore adminDS = new DataStore();
					adminDS.setUser(adminUser);
					adminDS.setPath("/");
					adminDS.setMode(DataStoreMode.Private);
					adminDS.setType(DataStoreType.Input_Output);
					adminDS.setName(adminUser.getUsername() + "_default");
					
					tx = em.getTransaction();			
					try{
						tx.begin();		
							em.persist(adminDS);		
						tx.commit();						
					}finally{
						if (tx !=null && tx.isActive()){ tx.rollback(); }
					}
				}
			}
		}finally{
			em.close(); 	
		}
		
		System.out.println("[DBInitializer] DB initialized.");
    }

	@SuppressWarnings("unchecked")
	private static List<User> getAdminUsers(EntityManager em) {
		List<User> toret = new LinkedList<User>();
		for (User u : (List<User>) em.createQuery("SELECT u FROM User u")
				.getResultList()) {
			if (u.isAdmin()) {
				toret.add(u);
			}
		}
		return toret;
	}
    
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        initDatabase();
    }
    
    public static EntityManager getRUbioSeqEntityMananger(){
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("rubioseq-database");
        EntityManager em = emf.createEntityManager();	
    	return em;
    }

}
