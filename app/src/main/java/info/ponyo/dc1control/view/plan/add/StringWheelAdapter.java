package info.ponyo.dc1control.view.plan.add;

import com.contrarywind.adapter.WheelAdapter;

import java.util.List;

/**
 * @author zxq
 * @Date 2019/12/2.
 * @Description:
 */
public class StringWheelAdapter implements WheelAdapter<String> {
    private List<String> mData;

    public StringWheelAdapter(List<String> data) {
        this.mData = data;
    }

    @Override
    public int getItemsCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public String getItem(int index) {
        return mData.get(index);
    }

    @Override
    public int indexOf(String o) {
        return mData.indexOf(o);
    }

    public List<String> getData() {
        return mData;
    }

    public StringWheelAdapter setData(List<String> data) {
        this.mData = data;
        notifyAll();
        return this;
    }
}
