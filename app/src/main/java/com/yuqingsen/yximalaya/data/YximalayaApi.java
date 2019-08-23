package com.yuqingsen.yximalaya.data;

import com.ximalaya.ting.android.opensdk.constants.DTransferConstants;
import com.ximalaya.ting.android.opensdk.datatrasfer.CommonRequest;
import com.ximalaya.ting.android.opensdk.datatrasfer.IDataCallBack;
import com.ximalaya.ting.android.opensdk.model.album.GussLikeAlbumList;
import com.ximalaya.ting.android.opensdk.model.album.SearchAlbumList;
import com.ximalaya.ting.android.opensdk.model.track.TrackList;
import com.ximalaya.ting.android.opensdk.model.word.HotWordList;
import com.ximalaya.ting.android.opensdk.model.word.SuggestWords;
import com.yuqingsen.yximalaya.utils.Constants;

import java.util.HashMap;
import java.util.Map;

public class YximalayaApi {

    private YximalayaApi(){
    }
    private static YximalayaApi sYximalayaApi;
    public short YximalayaApi;

    public static YximalayaApi getYximalayaApi() {
        if (sYximalayaApi == null) {
            synchronized (YximalayaApi.class){
                if (sYximalayaApi==null) {
                    sYximalayaApi = new YximalayaApi();
                }
            }
        }
        return sYximalayaApi;
    }

    /**
     * 获取推荐内容
     * @param callBack 请求结果的回调接口
     */
    public void getRecommendList(IDataCallBack<GussLikeAlbumList> callBack) {
        Map<String, String> map = new HashMap<>();
        //一页数据返回条数
        map.put(DTransferConstants.LIKE_COUNT, Constants.COUNT_RECOMMEND+"");
        CommonRequest.getGuessLikeAlbum(map,callBack);
    }

    /**
     * 根据专辑ID获取到专辑内容
     * @param callBack 获取专辑详情的结果回调接口
     * @param albumId 专辑ID
     * @param pageIndex 第几页
     */
    public void getAlbumDetail(IDataCallBack<TrackList> callBack,long albumId,int pageIndex){
        Map<String, String> map = new HashMap<String, String>();
        map.put(DTransferConstants.ALBUM_ID,albumId+"");
        map.put(DTransferConstants.SORT, "asc");
        map.put(DTransferConstants.PAGE, pageIndex + "");
        map.put(DTransferConstants.PAGE_SIZE, Constants.COUNT_DEFAULT + "");
        CommonRequest.getTracks(map,callBack);
    }

    /**
     * 根据关键字进行搜索。
     * @param keyword
     */
    public void searchByKeyword(String keyword, int page, IDataCallBack<SearchAlbumList> callBack) {
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY,keyword);
        map.put(DTransferConstants.PAGE,page+"");
        map.put(DTransferConstants.PAGE_SIZE,Constants.COUNT_RECOMMEND+"");
        CommonRequest.getSearchedAlbums(map,callBack);
    }

    /**
     * 获取推荐的热词
     * @param callBack
     */
    public void getHotWords(IDataCallBack<HotWordList> callBack){
        Map<String,String> map = new HashMap<>();
        map.put(DTransferConstants.TOP,Constants.COUNT_HOT_WORD+"");
        CommonRequest.getHotWords(map,callBack);
    }

    /**
     * 根据关键字获取联想词
     * @param keyword
     * @param callBack
     */
    public void getSuggestWord(String keyword, IDataCallBack<SuggestWords> callBack){
        Map<String, String> map = new HashMap<>();
        map.put(DTransferConstants.SEARCH_KEY, keyword);
        CommonRequest.getSuggestWord(map,callBack);
    }
}
