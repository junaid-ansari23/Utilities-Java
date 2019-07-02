package samples;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @author juansari
 * 
 * Thread safe implementation of LRU cache using concurrent Map & Queue.This also provides a cleanup method that can be called when the oldest entry
 * is removed from cache.
 *
 * @param <K>
 * @param <V>
 */
public class LRUCache<K, V> {

	private final int maxSize;
	private ConcurrentHashMap<K, V> map;
	private ConcurrentLinkedQueue<K> queue;
	private boolean cleanupRequired;

	public LRUCache(final int maxSize,boolean cleanupRequired) {
		this.maxSize = maxSize;
		this.map = new ConcurrentHashMap<K, V>(maxSize);
		this.queue = new ConcurrentLinkedQueue<K>();
		this.cleanupRequired=cleanupRequired;
	}

	public void put(final K key, final V value) {
		if (map.containsKey(key)) {
			// remove the key from the FIFO queue
			queue.remove(key);
		}

		while (queue.size() >= maxSize) {
			K oldestKey = queue.poll();
			if (null != oldestKey) {
				V oldestValue = map.get(oldestKey);
				map.remove(oldestKey);
				// call your cleanup method
				if(cleanupRequired) {
					doCleanUp(oldestValue);	
				}				
			}
		}
		queue.add(key);
		map.put(key, value);
	}

	public V get(final K key) {

		if (map.containsKey(key)) {
			// remove from queue and add it again in FIFO queue
			queue.remove(key);
			queue.add(key);
		}
		return map.get(key);
	}

	public void doCleanUp(V oldestValue) {
		//put your cleanup code here that just need value
	}
	public void doCleanUp(V oldestValue,K oldestKey) {
		//put your cleanup code here that need both key & value
	}

	public void addPoolOnStartup(final K key, final V conn) {
		queue.add(key);
		map.put(key, conn);
	}

	public boolean isCacheFull(final K key) {
		return (queue.size() >= maxSize);
	}

	public static void main(String[] args) {

		LRUCache<Integer, String> cache = new LRUCache<>(5,true);
		cache.put(1, "A");
		cache.put(2, "B");
		cache.put(3, "C");
		cache.put(4, "D");
		cache.put(5, "E");

		// key 5 moved ahead
		System.out.println(cache.get(5));

		// put new element to cache. this will remove key 1 from cache
		cache.put(6, "F");

		// this will print null
		System.out.println(cache.get(1));

	}

}
