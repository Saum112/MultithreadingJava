import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BoundedBufferMutex {

	public static void main(String[] args) throws InterruptedException {

		final BlockingQueueWithMutex<Integer> q = new BlockingQueueWithMutex<Integer>(5);

		Thread producer1 = new Thread(new Runnable() {

			@Override
			public void run() {

				for (int i = 1; i <= 10; i++) {
					try {
						q.enqueue(i);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Producer 1 enqueued " + i);
				}

			}
		});

		Thread producer2 = new Thread(new Runnable() {

			@Override
			public void run() {

				for (int i = 10; i <= 30; i++) {

					try {
						q.enqueue(i);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					System.out.println("Producer 2 enqueued " + i);
				}
			}
		});

		Thread consumer1 = new Thread(new Runnable() {
			public void run() {
				for (int i = 0; i < 20; i++) {
					try {
						System.out.println("Consumer thread 1 dequeued " + q.dequeue());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		Thread consumer2 = new Thread(new Runnable() {
			public void run() {

				for (int i = 0; i < 20; i++) {
					try {
						System.out.println("Consumer thread 2 dequeued " + q.dequeue());
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

			}
		});

		// Start threads
		producer1.start();
		producer2.start();
		consumer1.start();
		consumer2.start();

		// Wait for threads to finish
		producer1.join();
		producer2.join();
		consumer1.join();
		consumer2.join();

	}
}

class BlockingQueueWithMutex<T> {
	T[] array;
	Lock lock = new ReentrantLock();
	int size = 0;
	int capacity;
	int head = 0;
	int tail = 0;

	@SuppressWarnings("unchecked")
	public BlockingQueueWithMutex(int capacity) {
		// The casting results in a warning
		array = (T[]) new Object[capacity];
		this.capacity = capacity;
	}

	public T dequeue() throws InterruptedException {

		T item = null;

		lock.lock();
		while (size == 0) {
			lock.unlock();
			lock.lock();
		}

		if (head == capacity) {
			head = 0;
		}

		item = array[head];
		array[head] = null;
		head++;
		size--;

		lock.unlock();
		return item;
	}

	public void enqueue(T item) throws InterruptedException {

		lock.lock();
		while (size == capacity) {
			// Release the mutex to give other threads
			lock.unlock();
			// Reacquire the mutex before checking the
			// condition
			lock.lock();
		}

		if (tail == capacity) {
			tail = 0;
		}

		array[tail] = item;
		size++;
		tail++;
		lock.unlock();
	}
}
