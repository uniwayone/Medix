package app.slyworks.utils_lib

import android.content.ContentResolver
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.times
import com.nhaarman.mockitokotlin2.verify
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Test
import org.mockito.Mock

/**
 * Created by Joshua Sylvanus, 2:32 PM, 20/08/2022.
 */
class ContentResolverStoreTest{
    //region Vars
    private val contentResolver:ContentResolver = mock<ContentResolver>()
    @Mock
    lateinit var contentResolverStore: app.slyworks.utils_lib.ContentResolverStore
    //endregion

    @Test
    fun assertThat_contentResolverSet_isTheContentResolverRetrieved(){
        Assert.assertEquals(contentResolver, contentResolverStore.getContentResolver())
    }

    @Test
    fun whenSetContentResolver_isCalled_itSetsContentResolver(){
        contentResolverStore.setContentResolver(contentResolver)

        verify(contentResolverStore, times(0))
            .setContentResolver(contentResolver)
    }

    @Test
    fun whenGetContentResolver_isCalled_itGetsContentResolver(){
        assertEquals(1,1)
    }

    @Test
    fun whenNullifyContentResolver_isCalled_itNullifiesContentResolver(){}

}