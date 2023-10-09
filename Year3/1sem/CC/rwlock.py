import threading

class ReadWriteLock:
    def __init__(self):
        self.lock = threading.RLock()
        self.read_cond = threading.Condition(self.lock)
        self.write_cond = threading.Condition(self.lock)
        self.nr_readers = 0
        self.writer = False
    
    def readLock(self):
        self.lock.acquire()
        while(self.writer):
            self.read_cond.wait()
        self.nr_readers+=1
        self.lock.release()

    def readUnlock(self):
        self.lock.acquire()
        self.nr_readers-=1
        if self.nr_readers==0:
            self.read_cond.notify()
        self.lock.release()
    
    def writerLock(self):
        self.lock.acquire()
        while self.writer:
            self.write_cond.wait()
        self.writer= True
        while self.nr_readers>0:
            self.read_cond.wait()
        self.lock.release()

    def writerUnlock(self):
        self.lock.acquire()
        self.writer= False
        self.write_cond.notify_all()
        self.lock.release()
    