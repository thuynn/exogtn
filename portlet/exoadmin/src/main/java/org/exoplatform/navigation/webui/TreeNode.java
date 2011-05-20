package org.exoplatform.navigation.webui;

import org.exoplatform.portal.mop.Visibility;
import org.exoplatform.portal.mop.navigation.NavigationServiceException;
import org.exoplatform.portal.mop.navigation.NodeChangeListener;
import org.exoplatform.portal.mop.navigation.NodeState;
import org.exoplatform.portal.mop.user.UserNavigation;
import org.exoplatform.portal.mop.user.UserNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A wrapper class of {@link UserNode} for manipulation in WebUI part
 * 
 * @author <a href="mailto:trong.tran@exoplatform.com">Trong Tran</a>
 * @version $Revision$
 */
public class TreeNode implements NodeChangeListener<UserNode>
{
   private Map<String, TreeNode> caches;

   private UserNavigation nav;

   private UserNode node;

   private TreeNode rootNode;

   private boolean deleteNode = false;

   private boolean cloneNode = false;

   private String id;

   private List<TreeNode> children;

   public TreeNode(UserNavigation nav, UserNode node)
   {
      this(nav, node, null);
      this.rootNode = this;
      this.caches = new HashMap<String, TreeNode>();
      addToCached(this);
   }

   private TreeNode(UserNavigation nav, UserNode node, TreeNode rootNode)
   {
      this.rootNode = rootNode;
      this.nav = nav;
      this.node = node;
   }

   public List<TreeNode> getChildren()
   {
      if (children == null)
      {
         children = new ArrayList<TreeNode>();
         for (UserNode child : node.getChildren())
         {
            String key = child.getId() == null ? String.valueOf(child.hashCode()) : child.getId();
            TreeNode node = findNode(key);
            // This is for the first time a node is loaded
            if (node == null)
            {
               node = new TreeNode(nav, child, this.rootNode);
            }
            children.add(node);
         }
      }
      return children;
   }

   public TreeNode getChild(String name)
   {
      UserNode child = node.getChild(name);
      if (child == null)
      {
         return null;
      }
      return findNode(child.getId() == null ? String.valueOf(child.hashCode()) : child.getId());
   }

   public boolean removeChild(TreeNode child)
   {
      children = null;
      if (child == null)
      {
         return false;
      }
      removeFromCached(child);
      return node.removeChild(child.getName());
   }

   public TreeNode getParent()
   {
      UserNode parent = node.getParent();
      if (parent == null)
         return null;

      return findNode(parent.getId() == null ? String.valueOf(parent.hashCode()) : parent.getId());
   }

   public TreeNode getChild(int childIndex) throws IndexOutOfBoundsException
   {
      UserNode child = node.getChild(childIndex);
      if (child == null)
      {
         return null;
      }
      return findNode(child.getId() == null ? String.valueOf(child.hashCode()) : child.getId());
   }

   public TreeNode addChild(String childName)
   {
      children = null;
      UserNode child = node.addChild(childName);
      return addToCached(new TreeNode(nav, child, this.rootNode));
   }

   public void addChild(TreeNode child)
   {
      TreeNode oldParent = child.getParent();
      if (oldParent != null)
      {
         oldParent.children = null;
      }
      children = null; 
      this.node.addChild(child.getNode());
      addToCached(child);
   }
   
   public void addChild(int index, TreeNode child)
   {
      children = null;
      node.addChild(index, child.getNode());
      addToCached(child);
   }

   public void save() throws NavigationServiceException
   {
      this.rootNode.caches.clear();
      node.save();
   }

   public TreeNode findNode(String nodeID)
   {
      return this.rootNode.caches.get(nodeID);
   }

   public UserNode getNode()
   {
      return node;
   }

   public void setNode(UserNode node)
   {
      if (node == null)
      {
         throw new IllegalArgumentException("node can't be null");
      }
      children = null;
      this.node = node;
   }

   public UserNavigation getPageNavigation()
   {
      return nav;
   }

   public boolean isDeleteNode()
   {
      return deleteNode;
   }

   public void setDeleteNode(boolean deleteNode)
   {
      this.deleteNode = deleteNode;
   }

   public boolean isCloneNode()
   {
      return cloneNode;
   }

   public void setCloneNode(boolean b)
   {
      cloneNode = b;
   }

   public String getPageRef()
   {
      return node.getPageRef();
   }

   public String getId()
   {
      if (this.id == null)
      {
         this.id = node.getId() == null ? String.valueOf(node.hashCode()) : node.getId();
      }
      return this.id;
   }

   public String getURI()
   {
      return node.getURI();
   }

   public String getIcon()
   {
      return node.getIcon();
   }

   public void setIcon(String icon)
   {
      node.setIcon(icon);
   }

   public String getEncodedResolvedLabel()
   {
      String encodedLabel = node.getEncodedResolvedLabel();
      return encodedLabel == null ? "" : encodedLabel;
   }

   public String getName()
   {
      return node.getName();
   }

   public void setName(String name)
   {
      node.setName(name);
   }

   public String getLabel()
   {
      return node.getLabel();
   }

   public void setLabel(String label)
   {
      node.setLabel(label);
   }

   public Visibility getVisibility()
   {
      return node.getVisibility();
   }

   public void setVisibility(Visibility visibility)
   {
      node.setVisibility(visibility);
   }

   public long getStartPublicationTime()
   {
      return node.getStartPublicationTime();
   }

   public void setStartPublicationTime(long startPublicationTime)
   {
      node.setStartPublicationTime(startPublicationTime);
   }

   public long getEndPublicationTime()
   {
      return node.getEndPublicationTime();
   }

   public void setEndPublicationTime(long endPublicationTime)
   {
      node.setEndPublicationTime(endPublicationTime);
   }

   public void setPageRef(String pageRef)
   {
      node.setPageRef(pageRef);
   }

   public String getResolvedLabel()
   {
      String resolvedLabel = node.getResolvedLabel();
      return resolvedLabel == null ? "" : resolvedLabel;
   }

   public boolean hasChildrenRelationship()
   {
      return node.hasChildrenRelationship();
   }

   public int getChildrenCount()
   {
      return node.getChildrenCount();
   }

   private TreeNode addToCached(TreeNode node)
   {
      if (node == null)
      {
         return null;
      }

      this.rootNode.caches.put(node.getId(), node);
      if (node.hasChildrenRelationship())
      {
         for (TreeNode child : node.getChildren())
         {
            addToCached(child);
         }
      }
      return node;
   }

   private TreeNode removeFromCached(TreeNode node)
   {
      if (node == null)
      {
         return null;
      }

      this.rootNode.caches.remove(node.getId());
      if (node.hasChildrenRelationship())
      {
         for (TreeNode child : node.getChildren())
         {
            removeFromCached(child);
         }
      }
      return node;
   }

   @Override
   public void onAdd(UserNode source, UserNode parent, UserNode previous)
   {
      addToCached(new TreeNode(this.nav, source, this.rootNode));
      findNode(parent.getId()).setNode(parent);
   }

   @Override
   public void onCreate(UserNode source, UserNode parent, UserNode previous, String name)
   {
   }

   @Override
   public void onRemove(UserNode source, UserNode parent)
   {
      removeFromCached(findNode(source.getId()));
      findNode(parent.getId()).setNode(parent);
   }

   @Override
   public void onDestroy(UserNode source, UserNode parent)
   {
   }

   @Override
   public void onRename(UserNode source, UserNode parent, String name)
   {
   }

   @Override
   public void onUpdate(UserNode source, NodeState state)
   {
   }

   @Override
   public void onMove(UserNode source, UserNode from, UserNode to, UserNode previous)
   {
      TreeNode fromTreeNode = findNode(from.getId());
      TreeNode toTreeNode = findNode(to.getId());
      fromTreeNode.children = null;
      toTreeNode.children = null;
   }
}