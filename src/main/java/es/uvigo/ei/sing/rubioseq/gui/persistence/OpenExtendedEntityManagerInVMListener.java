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
package es.uvigo.ei.sing.rubioseq.gui.persistence;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceContextType;

import org.jboss.logging.Logger;
import org.zkoss.bind.BindComposer;
import org.zkoss.zk.ui.Component;
import org.zkoss.zk.ui.HtmlMacroComponent;
import org.zkoss.zk.ui.Page;
import org.zkoss.zk.ui.WebApp;
import org.zkoss.zk.ui.metainfo.ComponentInfo;
import org.zkoss.zk.ui.util.Composer;
import org.zkoss.zk.ui.util.ComposerExt;
import org.zkoss.zk.ui.util.FullComposer;
import org.zkoss.zk.ui.util.WebAppCleanup;
import org.zkoss.zk.ui.util.WebAppInit;

import es.uvigo.ei.sing.rubioseq.validation.EMInjectorConstraintValidatorFactory;

/**
 * 
 * @author dgpena
 *
 */
public class OpenExtendedEntityManagerInVMListener implements
		Composer<Component>, ComposerExt<Component>, FullComposer, WebAppInit,
		WebAppCleanup {

	private static final String DEFAULT_PERSISTENCE_UNIT_PREFERENCE = "OpenExtendedEntityManagerInVMListener.DefaultPersistenceUnitName";

	private static String defaultPersistenceUnitName = null;
	private static Logger LOGGER = Logger
			.getLogger(OpenExtendedEntityManagerInVMListener.class.getName());

	@Override
	public ComponentInfo doBeforeCompose(Page page, Component arg0,
			ComponentInfo compInfo) throws Exception {
		return compInfo;
	}

	@Override
	public void init(WebApp arg0) throws Exception {
		defaultPersistenceUnitName = arg0.getConfiguration().getPreference(
				DEFAULT_PERSISTENCE_UNIT_PREFERENCE, null);
	}

	@Override
	public void cleanup(WebApp wapp) throws Exception {
		EntityManagerFactoryMultiton.getInstance().close();
	}

	@Override
	public void doBeforeComposeChildren(Component arg0) throws Exception {
		injectComponent(arg0);
	}

	private void injectComponent(Component arg0) throws IllegalAccessException {
		Object ocomposer = arg0.getAttribute("$composer");
		if (ocomposer !=null && ocomposer instanceof BindComposer){
			BindComposer<?> composer = (BindComposer<?>) ocomposer;
			Object vmo = composer.getViewModel();
			if (vmo!=null){
				Class<?> clazz = vmo.getClass();
				//find PersistenceContext
				for (Field field: clazz.getDeclaredFields()){
					if (EntityManager.class.isAssignableFrom(field.getType())){
						for (Annotation annotation : field.getAnnotations()){
							if (annotation.annotationType().equals(PersistenceContext.class)){
								PersistenceContext annot = (PersistenceContext) annotation;
								if (annot.type() == PersistenceContextType.EXTENDED){
									String name = annot.unitName();
									if (name == null || name.equals("")){
										name = defaultPersistenceUnitName;
									}
									if (name != null){
										LOGGER.info("Injecting entity manager on "+vmo);
										EntityManager em = EntityManagerFactoryMultiton.getInstance().getEntityManagerFactory(name).createEntityManager();
										EMInjectorConstraintValidatorFactory.setThreadLocalEntityManager(em);
										field.setAccessible(true);
										field.set(vmo, em);
									}
									else {
										LOGGER.warn("Cannot inject PersistenceContext in View Model of class "
												+ clazz
												+ ". No persistence unit name found in the @PersistenceContext "
												+ "annotation nor the zk.xml preference "
												+ DEFAULT_PERSISTENCE_UNIT_PREFERENCE);
									}
									
								}else{
									LOGGER.warn("Cannot inject PersistenceContext in View Model of class "
											+ clazz
											+ ". Only @PersistenceContext(type=PersistenceContextType.EXTENDED)"
											+ " can be injected");
								}
							}
						}
					}
				}
			}
			
		}
	}

	@Override
	public boolean doCatch(Throwable ex) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void doFinally() throws Exception {
		// TODO Auto-generated method stub
	}

	@Override
	public void doAfterCompose(Component comp) throws Exception {
		if (comp instanceof HtmlMacroComponent) {
			List<Component> children = new LinkedList<Component>();
			getAllChildren(comp, children);
			for (Component child : children) {
				injectComponent(child);
			}
		}
	}

	private void getAllChildren(Component comp, List<Component> children) {
		children.addAll(comp.getChildren());
		for (Component child : comp.getChildren()) {
			getAllChildren(child, children);
		}
	}

}

class EntityManagerFactoryMultiton {

	private static Logger LOGGER = Logger
			.getLogger(EntityManagerFactoryMultiton.class.getName());
	private Map<String, EntityManagerFactory> emfs = new HashMap<String, EntityManagerFactory>();
	private static EntityManagerFactoryMultiton _instance = new EntityManagerFactoryMultiton();

	public static EntityManagerFactoryMultiton getInstance() {
		return _instance;
	}

	public void close() {
		for (EntityManagerFactory emf : emfs.values()) {
			try {
				emf.close();
			} catch (Throwable e) {
				LOGGER.error("Could not close EntityManagerFactory " + emf);
			}

		}

	}

	public EntityManagerFactory getEntityManagerFactory(String unitName) {

		if (!emfs.containsKey(unitName)) {
			emfs.put(unitName, Persistence.createEntityManagerFactory(unitName));
		}
		return emfs.get(unitName);
	}

}
