package book.twju.chapter_3;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Before;
import org.junit.Test;

public class Listing_5_Mockito_SessionStoreStub_TimelineTest {
  
  private static final FakeItem FIRST_ITEM = new FakeItem( 10 );
  private static final FakeItem SECOND_ITEM = new FakeItem( 20 );
  private static final FakeItem THIRD_ITEM = new FakeItem( 30 );
  
  private final static int NEW_FETCH_COUNT
    = new Timeline( new ItemProviderDummy() ).getFetchCount() + 1;

  private SessionStorage sessionStorage;
  private ItemProviderStub itemProvider;
  private Timeline timeline;

  @Before
  public void setUp() {
    itemProvider = new ItemProviderStub();
    sessionStorage = mock( SessionStorage.class );
    timeline = new Timeline( itemProvider, sessionStorage );
  }
  
  @Test
  public void fetchFirstItemsWithTopItemToRecover() {
    itemProvider.addItems( FIRST_ITEM, SECOND_ITEM, THIRD_ITEM );
    when( sessionStorage.readTop() ).thenReturn( SECOND_ITEM );
    timeline.setFetchCount( 1 );
      
    timeline.fetchItems();
    List<Item> actual = timeline.getItems();
      
    assertEquals( 1, actual.size() );
    assertSame( SECOND_ITEM, actual.get( 0 ) );
    verify( sessionStorage ).storeTop( SECOND_ITEM );
  }
  
  @Test
  public void fetchFirstItemsWithoutTopItemToRecover() {
    itemProvider.addItems( FIRST_ITEM, SECOND_ITEM, THIRD_ITEM );
    timeline.setFetchCount( 1 );
      
    timeline.fetchItems();
    List<Item> actual = timeline.getItems();
      
    assertEquals( 1, actual.size() );
    assertSame( THIRD_ITEM, actual.get( 0 ) );
    verify( sessionStorage ).storeTop( THIRD_ITEM );
  }
    
  @Test
  public void fetchItems() {
    itemProvider.addItems( FIRST_ITEM, SECOND_ITEM, THIRD_ITEM );
    timeline.setFetchCount( 1 );    
    timeline.fetchItems();

    timeline.fetchItems();
    List<Item> actual = timeline.getItems();
      
    assertArrayEquals( new Item[] { THIRD_ITEM, SECOND_ITEM }, 
                       actual.toArray( new Item[ 2 ] ) );
  }
  
  @Test
  public void setFetchCount() {
    timeline.setFetchCount( NEW_FETCH_COUNT );
    
    assertEquals( NEW_FETCH_COUNT, timeline.getFetchCount() );
  }
  
  @Test
  public void initialState() {
    assertTrue( timeline.getFetchCount() > 0 ); 
  }
  
  @Test
  public void setFetchCountExceedsLowerBound() {
    int originalFetchCount = timeline.getFetchCount();
      
    timeline.setFetchCount( Timeline.FETCH_COUNT_LOWER_BOUND - 1 );
      
    assertEquals( originalFetchCount, timeline.getFetchCount() );  
  }
    
  @Test
  public void setFetchCountExceedsUpperBound() {
    int originalFetchCount = timeline.getFetchCount();
      
    timeline.setFetchCount( Timeline.FETCH_COUNT_UPPER_BOUND + 1 );
      
    assertEquals( originalFetchCount, timeline.getFetchCount() );  
  }
}