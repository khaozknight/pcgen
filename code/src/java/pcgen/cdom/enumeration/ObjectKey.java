/*
 * Copyright 2005 (C) Tom Parker <thpr@sourceforge.net>
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
 * Created on June 18, 2005.
 *
 * Current Ver: $Revision: 513 $
 * Last Editor: $Author: soulcatcher $
 * Last Edited: $Date: 2006-03-29 12:17:43 -0500 (Wed, 29 Mar 2006) $
 */
package pcgen.cdom.enumeration;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.net.URI;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

import pcgen.base.util.CaseInsensitiveMap;
import pcgen.cdom.base.CDOMObject;
import pcgen.cdom.helper.ChoiceSet;
import pcgen.core.facade.AlignmentFacade;
import pcgen.core.facade.PCStatFacade;
import pcgen.util.enumeration.Load;
import pcgen.util.enumeration.Visibility;

/**
 * @author Tom Parker <thpr@sourceforge.net>
 * 
 * This is a Typesafe enumeration of legal String Characteristics of an object.
 */
public final class ObjectKey<T> {
	
	public static final ObjectKey<Region> REGION = new ObjectKey<Region>();

	public static final ObjectKey<SubRegion> SUBREGION = new ObjectKey<SubRegion>();

	public static final ObjectKey<Visibility> VISIBILITY = new ObjectKey<Visibility>();

	public static final ObjectKey<SubRace> SUBRACE = new ObjectKey<SubRace>();

	public static final ObjectKey<RaceType> RACETYPE = new ObjectKey<RaceType>();

	public static final ObjectKey<Boolean> REMOVABLE = new ObjectKey<Boolean>();

	public static final ObjectKey<Load> UNENCUMBERED_LOAD = new ObjectKey<Load>();

	public static final ObjectKey<Load> UNENCUMBERED_ARMOR = new ObjectKey<Load>();

	public static final ObjectKey<Boolean> SPELLBOOK = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> MOD_TO_SKILLS = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> MEMORIZE_SPELLS = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> IS_MONSTER = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> HAS_SPELL_FORMULA = new ObjectKey<Boolean>();

	public static final ObjectKey<Gender> GENDER_LOCK = new ObjectKey<Gender>();

	public static final ObjectKey<BigDecimal> COST = new ObjectKey<BigDecimal>();

	public static final ObjectKey<PCStatFacade> KEY_STAT = new ObjectKey<PCStatFacade>();

	public static final ObjectKey<PCStatFacade> SPELL_STAT = new ObjectKey<PCStatFacade>();

	public static final ObjectKey<PCStatFacade> BONUS_SPELL_STAT = new ObjectKey<PCStatFacade>();

	public static final ObjectKey<Boolean> READ_ONLY = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> COST_DOUBLE = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> ASSIGN_TO_ALL = new ObjectKey<Boolean>();

	public static final ObjectKey<BigDecimal> WEIGHT = new ObjectKey<BigDecimal>();

	public static final ObjectKey<Boolean> USE_MASTER_SKILL = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> STACKS = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> MULTIPLE_ALLOWED = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> USE_UNTRAINED = new ObjectKey<Boolean>();

	public static final ObjectKey<AlignmentFacade> ALIGNMENT = new ObjectKey<AlignmentFacade>();

	public static final ObjectKey<Boolean> EXCLUSIVE = new ObjectKey<Boolean>();

	public static final ObjectKey<BigDecimal> CONTAINER_WEIGHT_CAPACITY = new ObjectKey<BigDecimal>();

	public static final ObjectKey<Boolean> CONTAINER_CONSTANT_WEIGHT = new ObjectKey<Boolean>();

	public static final ObjectKey<AbilityCategory> CATEGORY = new ObjectKey<AbilityCategory>();

	public static final ObjectKey<Boolean> ATTACKS_PROGRESS = new ObjectKey<Boolean>();

	public static final ObjectKey<Type> SPELL_TYPE = new ObjectKey<Type>();

	public static final ObjectKey<Boolean> RETIRED = new ObjectKey<Boolean>();

	public static final ObjectKey<URI> SOURCE_URI = new ObjectKey<URI>();

	public static final ObjectKey<BigDecimal> FACE_WIDTH = new ObjectKey<BigDecimal>();

	public static final ObjectKey<BigDecimal> FACE_HEIGHT = new ObjectKey<BigDecimal>();

	public static final ObjectKey<ChoiceSet<?>> CHOICE = new ObjectKey<ChoiceSet<?>>();

	public static final ObjectKey<ChoiceSet<?>> KIT_CHOICE = new ObjectKey<ChoiceSet<?>>();

	public static final ObjectKey<CDOMObject> PSEUDO_PARENT = new ObjectKey<CDOMObject>();

	public static final ObjectKey<Boolean> HAS_BONUS_SPELL_STAT = new ObjectKey<Boolean>();

	public static final ObjectKey<BigDecimal>  PROHIBITED_COST = new ObjectKey<BigDecimal>();

	public static final ObjectKey<Boolean> USE_SPELL_SPELL_STAT = new ObjectKey<Boolean>();

	public static final ObjectKey<Boolean> CASTER_WITHOUT_SPELL_STAT = new ObjectKey<Boolean>();

	private static CaseInsensitiveMap<ObjectKey<?>> map = null;

	private ObjectKey() {
		// Only allow instantation here
	}

	public T cast(Object o) {
		return (T) o;
	}

	public static <OT> ObjectKey<OT> getKeyFor(Class<OT> c, String s) {
		if (map == null) {
			buildMap();
		}
		/*
		 * CONSIDER This is actually not type safe, there is a case of asking
		 * for a String a second time with a different Class that ObjectKey
		 * currently does not handle. Two solutions: One, store this in a
		 * Two-Key map and allow a String to map to more than one ObjectKey
		 * given different output types (considered confusing) or Two, store the
		 * Class and validate that with a an error message if a different class
		 * is requested.
		 */
		ObjectKey<OT> o = (ObjectKey<OT>) map.get(s);
		if (o == null) {
			o = new ObjectKey<OT>();
			map.put(s, o);
		}
		return o;
	}

	private static void buildMap() {
		map = new CaseInsensitiveMap<ObjectKey<?>>();
		Field[] fields = ObjectKey.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			int mod = fields[i].getModifiers();

			if (Modifier.isStatic(mod) && Modifier.isFinal(mod)
					&& Modifier.isPublic(mod)) {
				try {
					Object o = fields[i].get(null);
					if (o instanceof ObjectKey) {
						map.put(fields[i].getName(), (ObjectKey<?>) o);
					}
				} catch (IllegalArgumentException e) {
					throw new InternalError();
				} catch (IllegalAccessException e) {
					throw new InternalError();
				}
			}
		}
	}

	@Override
	public String toString() {
		/*
		 * CONSIDER Should this find a way to do a Two-Way Map or something to
		 * that effect?
		 */
		if (map == null)
		{
			buildMap();
		}
		for (Map.Entry<?, ObjectKey<?>> me : map.entrySet()) {
			if (me.getValue() == this) {
				return me.getKey().toString();
			}
		}
		// Error
		return "";
	}

	public static Collection<ObjectKey<?>> getAllConstants() {
		if (map == null) {
			buildMap();
		}
		return new HashSet<ObjectKey<?>>(map.values());
	}
}
