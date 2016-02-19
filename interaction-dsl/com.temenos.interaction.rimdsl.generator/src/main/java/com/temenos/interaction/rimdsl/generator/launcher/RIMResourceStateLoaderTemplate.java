package com.temenos.interaction.rimdsl.generator.launcher;

import com.google.common.io.Files;
import com.google.inject.Injector;
import com.temenos.interaction.core.hypermedia.ResourceState;
import com.temenos.interaction.core.loader.ResourceStateLoader;
import com.temenos.interaction.rimdsl.RIMDslStandaloneSetup;
import com.temenos.interaction.rimdsl.rim.DomainModel;
import org.eclipse.emf.ecore.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.temenos.interaction.rimdsl.generator.launcher.RimResourceTranslatorImpl.RimResourceTranslationTarget;

/**
 * @author kwieconkowski
 */

public abstract class RIMResourceStateLoaderTemplate implements ResourceStateLoader<String> {
    protected final RimParserSetup rimParserSetup;
    protected final RimResourceTranslator<RimResourceTranslationTarget> translator;

    private static final Logger logger = LoggerFactory.getLogger(RIMResourceStateLoaderTemplate.class);
    
    public RIMResourceStateLoaderTemplate() {
        rimParserSetup = ((Injector) new RIMDslStandaloneSetup().createInjectorAndDoEMFRegistration()).getInstance(RimParserSetup.class);
        translator = new RimResourceTranslatorImpl();
    }

    @Override
    public List<ResourceStateLoader.ResourceStateResult> load(String rimFileName) {
        if (rimFileName == null || rimFileName.isEmpty()) {
            throw new IllegalArgumentException("Passed argument is null or empty");
        }

        List<ResourceState> resourceStateList = getResourceStateList(rimFileName);
        Map<String, String> propertiesMap = getPropertiesMap(rimFileName);

        assert (resourceStateList.size() == propertiesMap.size());
        if (resourceStateList.isEmpty()) {
            return null;
        }

        return produceResourceStateResultList(resourceStateList, propertiesMap);
    }
    
	public List<ResourceStateLoader.ResourceStateResult> load(File rimFile) {
        if (rimFile == null || !rimFile.exists()) {
            throw new IllegalArgumentException("File not found");
        }

        List<ResourceState> resourceStateList = getResourceStateList(rimFile);
        Map<String, String> propertiesMap = getPropertiesMap(rimFile);

        assert (resourceStateList.size() == propertiesMap.size());
        if (resourceStateList.isEmpty()) {
            return null;
        }

        return produceResourceStateResultList(resourceStateList, propertiesMap);
	}
	
	protected abstract List<ResourceState> getResourceStateList(File rimFile);
	
    protected abstract Map<String, String> getPropertiesMap(File rimFile);

    protected abstract List<ResourceState> getResourceStateList(String rimFileName);

    protected abstract Map<String, String> getPropertiesMap(String rimFileName);

    protected List<ResourceStateLoader.ResourceStateResult> produceResourceStateResultList(List<ResourceState> resourceStateList, Map<String, String> propertiesMap) {
        String resourceStateIdBeanName, path, prefix;
        String[] methodAndPath, methods;
        ResourceState resourceState;

        prefix = getPrefixName(propertiesMap);
        List<ResourceStateLoader.ResourceStateResult> resourceStateResultList = new ArrayList<ResourceStateLoader.ResourceStateResult>();

        for (int i = 0; i < resourceStateList.size(); i++) {
            resourceState = resourceStateList.get(i);
            resourceStateIdBeanName = String.format("%s-%s", prefix, resourceState.getName());
            methodAndPath = propertiesMap.get(resourceStateIdBeanName).split(" ");
            methods = methodAndPath[0].split(",");
            path = methodAndPath[1];
            resourceStateResultList.add(new ResourceStateLoader.ResourceStateResult(resourceStateIdBeanName, resourceState, methods, path));
        }
        return resourceStateResultList;
    }

    protected String getPrefixName(Map<String, String> propertiesMap) {
        assert !propertiesMap.isEmpty();
        String randomKey = propertiesMap.keySet().iterator().next();
        return randomKey.substring(0, randomKey.lastIndexOf('-'));
    }

    protected Resource getResourceFromRimFile(String rimFileName) {
        DomainModel domainModel = rimParserSetup.parseToDomainModel(getFileContext(rimFileName));
        return domainModel.eResource();
    }
    
    protected Resource getResourceFromRimFile(File rimFile){
        DomainModel domainModel = rimParserSetup.parseToDomainModel(getFileContext(rimFile));
        return domainModel.eResource();
    }
    
    protected String getFileContext(File rimFile){
    	String content = null;
    	try {
			content = Files.toString(rimFile, StandardCharsets.UTF_8);
		} catch (IOException e) {
			logger.error("Unable to load RIM file");
		}
    	return content;
    }

    protected String getFileContext(String rimFileName) {
        try {
        	URL location = getClass().getClassLoader().getResource(rimFileName);
        	if(location != null){
        		return Files.toString(new File(location.getFile()), StandardCharsets.UTF_8);
        	}else{
        		return null;
        	}
            
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}