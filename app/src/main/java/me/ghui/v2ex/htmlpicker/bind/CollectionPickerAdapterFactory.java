package me.ghui.v2ex.htmlpicker.bind;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.Nullable;

import me.ghui.v2ex.htmlpicker.HtmlPicker;
import me.ghui.v2ex.htmlpicker.PickerAdapter;
import me.ghui.v2ex.htmlpicker.PickerAdapterFactory;
import me.ghui.v2ex.htmlpicker.annotations.Select;
import me.ghui.v2ex.htmlpicker.internal.Types;
import me.ghui.v2ex.htmlpicker.reflect.TypeToken;

/**
 * Created by ghui on 13/04/2017.
 */

public final class CollectionPickerAdapterFactory implements PickerAdapterFactory {

	@Override
	public <C> PickerAdapter<C> create(HtmlPicker htmlPicker, TypeToken<C> typeToken) {
		Type type = typeToken.getType();

		Class<? super C> rawType = typeToken.getRawType();
		if (!Collection.class.isAssignableFrom(rawType)) {
			return null;
		}

		Type elementType = Types.getCollectionElementType(type, rawType);
		PickerAdapter<?> elementAdapter = htmlPicker.getAdapter(TypeToken.get(elementType));
		@SuppressWarnings({"unchecked"})
		PickerAdapter<C> adapter = new Adapter(rawType, elementType, elementAdapter);
		return adapter;
	}

	private static final class Adapter<E> extends PickerAdapter<Collection<E>> {

		private Class<Collection<E>> type;
		private Type elementType;
		private PickerAdapter<E> elementAdapter;

		public Adapter(Class<Collection<E>> type, Type elementType, PickerAdapter<E> elementAdapter) {
			this.type = type;
			this.elementType = elementType;
			this.elementAdapter = elementAdapter;
		}

		@Override
		public Collection<E> read(Element element, @Nullable Picker picker) {
			List<E> list;
			if (ArrayList.class.isAssignableFrom(type)) {
				list = new ArrayList<>();
			} else if (LinkedList.class.isAssignableFrom(type)) {
				list = new LinkedList<>();
			} else {
				throw new UnsupportedOperationException("Only support ArrayList and LinkedList Collection type for now");
			}

			if (picker != null) {
				Elements elements = element.select(picker.getPath());
				for (Element e : elements) {
					E instance = elementAdapter.read(e, Picker.create(elementType.getClass().getAnnotation(Select.class)));
					list.add(instance);
				}
			}
			return list;
		}

	}

}
