package net.flexmojos.oss.plugin.flexbuilder;

import org.apache.maven.plugin.ide.IdeDependency;
import org.codehaus.plexus.util.StringUtils;

import net.flexmojos.oss.plugin.common.FlexScopes;
import net.flexmojos.oss.plugin.flexbuilder.sdk.Kind;
import net.flexmojos.oss.plugin.flexbuilder.sdk.LinkType;
import net.flexmojos.oss.plugin.flexbuilder.sdk.LocalSdkEntry;
import static net.flexmojos.oss.plugin.common.FlexExtension.ANE;

/**
 * Extends IdeDependency to add the scope value. Additionally this extension handles resolving the path to the
 * dependency to fix a problem that was encountered with 64bit Windows.
 * 
 * @author Lance Linder llinder@gmail.com
 */
public class FbIdeDependency
    extends IdeDependency
{
    private String path = null;

    private String sourcePath = null;

    private String rslUrlTemplate = null;

    private String policyFileUrlTemplate = null;

    private LocalSdkEntry localSdkEntry = null;

    /**
     * Constructor that copies values from an existing IdeDependency instance and addes the scope value
     * 
     * @param dep
     * @param scope
     */
    public FbIdeDependency( IdeDependency dep, String scope )
    {
        this( dep, scope, null );
    }

    public FbIdeDependency( IdeDependency dep, String scope, LocalSdkEntry entry )
    {
        this.setArtifactId( dep.getArtifactId() );
        this.setClassifier( dep.getClassifier() );
        this.setFile( dep.getFile() );
        this.setGroupId( dep.getGroupId() );
        this.setScope( scope );
        this.setType( dep.getType() );
        this.setVersion( dep.getVersion() );
        this.setEclipseProjectName( dep.getEclipseProjectName() );
        this.setAddedToClasspath( dep.isAddedToClasspath() );
        if ( dep.getSourceAttachment() != null )
            this.setSourceAttachment( dep.getSourceAttachment().getAbsoluteFile() );
        this.setReferencedProject( dep.isReferencedProject() );
        this.localSdkEntry = entry;
    }

    /**
     * dependency scope
     */
    private String scope;

    /**
     * Getter for <code>scope</code>.
     * 
     * @return Returns the scope.
     */
    public String getScope()
    {
        return this.scope;
    }

    /**
     * Setter for <code>scope</code>.
     * 
     * @param scope The scope to set.
     */
    public void setScope( String scope )
    {
        this.scope = scope;
    }

    public void setLocalSdkEntry( LocalSdkEntry entry )
    {
        localSdkEntry = entry;
    }

    public LocalSdkEntry getLocalSdkEntry()
    {
        return localSdkEntry;
    }

    public Integer getLinkTypeId()
    {
        return getLinkType().getId();
    }

    public LinkType getLinkType()
    {
        LinkType type = LinkType.MERGE;

        if ( scope.equals( FlexScopes.EXTERNAL ) || scope.equals( "runtime" ) || ANE.equalsIgnoreCase(getType()) )
        {
            type = LinkType.EXTERNAL;
        }
        else if ( scope.equals( FlexScopes.RSL ) )
        {
            type = LinkType.RSL;
        }
        else if ( scope.equals( FlexScopes.CACHING ) )
        {
            type = LinkType.RSL_DIGEST;
        }
        else
        {
            type = LinkType.MERGE; // MERGED is 1. MERGED is default.
        }

        return type;
    }
    
    public Integer getKindId()
    {
        return getKind().getId();
    }
    
    public Kind getKind() 
    {
    	Kind kind = Kind.getDefaultKind();
    	
    	if(ANE.equalsIgnoreCase(getType())) 
    	{
    		kind = Kind.PACKAGE;
    	}
    	
    	return kind;
    }

    public String getPolicyFileUrl()
    {
        String url = policyFileUrlTemplate;

        url = StringUtils.replace( url, "{groupId}", getGroupId() );
        url = StringUtils.replace( url, "{artifactId}", getArtifactId() );
        url = StringUtils.replace( url, "{version}", getVersion() );

        return url;
    }

    public void setPolicyFileUrl( String template )
    {
        policyFileUrlTemplate = template;
    }

    public String getRslUrl( String extension )
    {
        String url = rslUrlTemplate;

        url = StringUtils.replace( url, "{groupId}", getGroupId() );
        url = StringUtils.replace( url, "{artifactId}", getArtifactId() );
        url = StringUtils.replace( url, "{version}", getVersion() );
        url = StringUtils.replace( url, "{extension}", extension );

        return url;
    }

    public void setRslUrl( String template )
    {
        rslUrlTemplate = template;
    }

    public void setPath( String path )
    {
        this.path = path;
    }

    public String getPath()
    {
        if ( localSdkEntry != null )
        {
            return localSdkEntry.getPath();
        }
        else if ( path == null )
        {
            path = this.getFile().getAbsolutePath();
        }
        return path;
    }

    public void setSourcePath( String path )
    {
        this.sourcePath = path;
    }

    public String getSourcePath()
    {
        if ( localSdkEntry != null )
        {
            return localSdkEntry.getSourcePath();
        }

        return sourcePath;
    }

    public boolean isFlexSdkDependency()
    {
        return ( localSdkEntry != null || ( "rb.swc".equals( getType() ) && "com.adobe.flex.framework".equals( getGroupId() ) ) );
    }

    public boolean isModifiedFlexSdkDependency()
    {
        boolean modified = false;

        if ( isFlexSdkDependency() )
        {
            // TODO resolve if this dependency is different from the base.
        }

        return modified;
    }

}
