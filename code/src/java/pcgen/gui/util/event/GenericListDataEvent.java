/*
 * GenericListDataEvent.java
 * Copyright 2008 Connor Petty <cpmeister@users.sourceforge.net>
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 * 
 * Created on Jul 2, 2008, 5:29:07 PM
 */
package pcgen.gui.util.event;

import java.util.Collection;
import javax.swing.event.ListDataEvent;

/**
 *
 * @author Connor Petty <cpmeister@users.sourceforge.net>
 */
public class GenericListDataEvent extends ListDataEvent
{

    private Collection<? extends Object> data;
    private boolean isAdjusting;

    public GenericListDataEvent(Object source, Collection<? extends Object> data,
                                 boolean isAdjusting,
                                 int type, int index0, int index1)
    {
        super(source, type, index0, index1);
        this.data = data;
        this.isAdjusting = isAdjusting;
    }

    /**
     * Returns a collection containing added or removed data.
     * In the case of changed data, this will return the overriden data.
     * @return a collection containing added or removed data
     */
    public Collection<? extends Object> getData()
    {
        return data;
    }

    /**
     * Returns true if this is one of multiple change events.
     * @return true if this is one of a rapid series of events
     */
    public boolean getValueIsAdjusting()
    {
        return isAdjusting;
    }

}
