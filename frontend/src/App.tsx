import React, { useState, useEffect } from 'react';
import axios from 'axios';
import { 
  Activity, Users, Server, AlertCircle, CheckCircle2, 
  Clock, PlayCircle, Settings, LayoutDashboard, Database,
  ArrowRight, Plus, RefreshCw, XCircle
} from 'lucide-react';
import { 
  LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer,
  AreaChart, Area, BarChart, Bar
} from 'recharts';

export default function App() {
  const [activeTab, setActiveTab] = useState('dashboard');
  const [metrics, setMetrics] = useState({
    activeWorkers: 0,
    jobsExecuting: 0,
    completedToday: 0,
    failedJobs: 0,
    totalQueues: 0
  });

  const [chartData, setChartData] = useState([]);
  const [jobs, setJobs] = useState([]);
  const [queues, setQueues] = useState([]);
  
  useEffect(() => {
    const fetchMetrics = async () => {
      try {
        const response = await axios.get('http://localhost:8080/api/dashboard/metrics');
        setMetrics(response.data);
        
        setChartData(prev => {
          const newPoint = { 
            time: new Date().toLocaleTimeString([], {hour: '2-digit', minute:'2-digit', second:'2-digit'}),
            jobs: response.data.completedToday + response.data.jobsExecuting,
            success: response.data.completedToday,
            failed: response.data.failedJobs
          };
          const newData = [...prev, newPoint];
          if (newData.length > 10) newData.shift();
          return newData;
        });
      } catch (error) {
        // Silently ignore during start up
      }
    };

    fetchMetrics();
    const interval = setInterval(fetchMetrics, 2000);
    return () => clearInterval(interval);
  }, []);

  const fetchJobs = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/jobs');
      setJobs(res.data);
    } catch (e) {}
  };

  const fetchQueues = async () => {
    try {
      const res = await axios.get('http://localhost:8080/api/queues');
      setQueues(res.data);
    } catch (e) {}
  };

  useEffect(() => {
    if (activeTab === 'jobs') fetchJobs();
    if (activeTab === 'queues') fetchQueues();
  }, [activeTab]);

  const handleCreateJob = async () => {
    try {
      await axios.post('http://localhost:8080/api/jobs', {
        name: "Test Job " + Math.floor(Math.random() * 1000),
        payload: "{}",
        status: "PENDING"
      });
      if (activeTab === 'jobs') fetchJobs();
    } catch (e) {}
  };

  return (
    <div className="flex h-screen bg-[#09090b] text-zinc-100 overflow-hidden font-sans">
      {/* Sidebar */}
      <div className="w-64 bg-[#09090b] border-r border-zinc-800 flex flex-col">
        <div className="p-6 flex items-center gap-3 border-b border-zinc-800">
          <div className="w-8 h-8 rounded-lg bg-blue-600 flex items-center justify-center shadow-lg shadow-blue-500/20">
            <Activity size={20} className="text-white" />
          </div>
          <h1 className="text-xl font-bold tracking-tight">Codity<span className="text-blue-500">Jobs</span></h1>
        </div>
        
        <div className="flex-1 py-6 px-3 space-y-1">
          <NavItem icon={<LayoutDashboard size={18}/>} label="Dashboard" active={activeTab === 'dashboard'} onClick={() => setActiveTab('dashboard')} />
          <NavItem icon={<Server size={18}/>} label="Queues" active={activeTab === 'queues'} onClick={() => setActiveTab('queues')} />
          <NavItem icon={<PlayCircle size={18}/>} label="Jobs" active={activeTab === 'jobs'} onClick={() => setActiveTab('jobs')} />
          <NavItem icon={<Users size={18}/>} label="Workers" active={activeTab === 'workers'} onClick={() => setActiveTab('workers')} />
          <NavItem icon={<Database size={18}/>} label="Dead Letter" active={activeTab === 'dlq'} onClick={() => setActiveTab('dlq')} />
        </div>
      </div>

      {/* Main Content */}
      <div className="flex-1 overflow-y-auto bg-[#09090b]">
        <header className="h-16 border-b border-zinc-800 bg-[#09090b]/80 backdrop-blur-md sticky top-0 z-10 flex items-center justify-between px-8">
          <h2 className="text-xl font-semibold capitalize tracking-tight">{activeTab.replace('-', ' ')}</h2>
          <div className="flex items-center gap-4">
            <button onClick={handleCreateJob} className="bg-blue-600 hover:bg-blue-500 text-white px-4 py-2 rounded-lg text-sm font-medium flex items-center gap-2 transition-all shadow-lg shadow-blue-600/20 active:scale-95">
              <Plus size={16} /> Create Job
            </button>
            <div className="h-9 w-9 rounded-full bg-zinc-800 border border-zinc-700 flex items-center justify-center shadow-inner cursor-pointer hover:bg-zinc-700 transition-colors">
              <span className="text-sm font-semibold text-zinc-300">AD</span>
            </div>
          </div>
        </header>

        <main className="p-8">
          {activeTab === 'dashboard' && (
            <div className="space-y-8 animate-in fade-in duration-500 slide-in-from-bottom-4">
              {/* Stats Grid */}
              <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
                <StatCard title="Active Workers" value={metrics.activeWorkers} icon={<Server className="text-emerald-500"/>} trend="Ready for execution" />
                <StatCard title="Jobs Executing" value={metrics.jobsExecuting} icon={<Activity className="text-blue-500"/>} trend="Running in Virtual Threads" />
                <StatCard title="Completed Today" value={metrics.completedToday} icon={<CheckCircle2 className="text-indigo-500"/>} trend="Successfully finished" />
                <StatCard title="Failed Jobs" value={metrics.failedJobs} icon={<AlertCircle className="text-rose-500"/>} trend="Sent to Dead Letter" />
              </div>

              {/* Charts Section */}
              <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
                <div className="lg:col-span-2 bg-[#121214] border border-zinc-800/80 rounded-2xl p-6 shadow-xl shadow-black/20">
                  <h3 className="text-lg font-semibold mb-6 flex items-center gap-2 text-zinc-100">
                    <Activity size={18} className="text-blue-500"/> Live Execution Throughput
                  </h3>
                  <div className="h-[320px] w-full">
                    <ResponsiveContainer width="100%" height="100%">
                      <AreaChart data={chartData} margin={{ top: 10, right: 10, left: -20, bottom: 0 }}>
                        <defs>
                          <linearGradient id="colorJobs" x1="0" y1="0" x2="0" y2="1">
                            <stop offset="5%" stopColor="#3b82f6" stopOpacity={0.4}/>
                            <stop offset="95%" stopColor="#3b82f6" stopOpacity={0}/>
                          </linearGradient>
                        </defs>
                        <CartesianGrid strokeDasharray="3 3" stroke="#27272a" vertical={false} />
                        <XAxis dataKey="time" stroke="#71717a" fontSize={12} tickLine={false} axisLine={false} />
                        <YAxis stroke="#71717a" fontSize={12} tickLine={false} axisLine={false} />
                        <Tooltip 
                          contentStyle={{ backgroundColor: '#18181b', borderColor: '#27272a', borderRadius: '12px', boxShadow: '0 10px 15px -3px rgb(0 0 0 / 0.5)' }}
                          itemStyle={{ color: '#e4e4e7', fontWeight: 500 }}
                        />
                        <Area type="monotone" dataKey="jobs" stroke="#3b82f6" strokeWidth={3} fillOpacity={1} fill="url(#colorJobs)" isAnimationActive={false} />
                      </AreaChart>
                    </ResponsiveContainer>
                  </div>
                </div>

                <div className="bg-[#121214] border border-zinc-800/80 rounded-2xl p-6 shadow-xl shadow-black/20 flex flex-col">
                  <h3 className="text-lg font-semibold mb-6 flex items-center gap-2 text-zinc-100">
                     <Server size={18} className="text-emerald-500"/> Queue Health
                  </h3>
                  <div className="flex-1 space-y-4 overflow-y-auto pr-2">
                    <QueueItem name="default-queue" pending={metrics.jobsExecuting} workers={metrics.activeWorkers} status="healthy" />
                    <QueueItem name="email-notifications" pending={0} workers={0} status="healthy" />
                    <QueueItem name="data-sync" pending={12} workers={2} status="warning" />
                    <QueueItem name="analytics-aggregation" pending={0} workers={5} status="healthy" />
                  </div>
                </div>
              </div>
            </div>
          )}

          {activeTab === 'jobs' && (
             <div className="bg-[#121214] border border-zinc-800/80 rounded-2xl shadow-xl shadow-black/20 overflow-hidden animate-in fade-in duration-500">
               <div className="px-6 py-4 border-b border-zinc-800/80 flex justify-between items-center bg-zinc-900/30">
                 <h3 className="font-semibold text-lg text-zinc-100">All Jobs</h3>
                 <button onClick={fetchJobs} className="text-zinc-400 hover:text-white transition-colors p-2 rounded-lg hover:bg-zinc-800"><RefreshCw size={18}/></button>
               </div>
               <div className="overflow-x-auto">
                 <table className="w-full text-left text-sm">
                   <thead className="bg-zinc-900/50 text-zinc-400 font-medium">
                     <tr>
                       <th className="px-6 py-4">ID</th>
                       <th className="px-6 py-4">Name</th>
                       <th className="px-6 py-4">Status</th>
                       <th className="px-6 py-4">Created At</th>
                     </tr>
                   </thead>
                   <tbody className="divide-y divide-zinc-800/80">
                     {jobs.length === 0 ? (
                       <tr><td colSpan="4" className="px-6 py-8 text-center text-zinc-500">No jobs found. Create one to get started.</td></tr>
                     ) : jobs.map(job => (
                       <tr key={job.id} className="hover:bg-zinc-800/30 transition-colors">
                         <td className="px-6 py-4 font-mono text-zinc-400">#{job.id}</td>
                         <td className="px-6 py-4 font-medium">{job.name}</td>
                         <td className="px-6 py-4">
                           <StatusBadge status={job.status} />
                         </td>
                         <td className="px-6 py-4 text-zinc-400">{new Date(job.createdAt).toLocaleString()}</td>
                       </tr>
                     ))}
                   </tbody>
                 </table>
               </div>
             </div>
          )}

          {activeTab === 'queues' && (
             <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6 animate-in fade-in duration-500">
                {queues.length === 0 ? (
                  <div className="col-span-full py-12 text-center text-zinc-500">No queues fetched. Loading...</div>
                ) : queues.map(q => (
                  <div key={q.id} className="bg-[#121214] border border-zinc-800/80 rounded-2xl p-6 shadow-lg shadow-black/20 hover:border-blue-500/50 transition-colors group cursor-pointer">
                    <div className="flex justify-between items-start mb-4">
                      <div className="p-3 bg-blue-500/10 rounded-xl group-hover:bg-blue-500/20 transition-colors">
                        <Database className="text-blue-400" size={24} />
                      </div>
                      <span className="px-2.5 py-1 rounded-full bg-zinc-800 text-xs font-medium text-zinc-300 border border-zinc-700">{q.type}</span>
                    </div>
                    <h3 className="text-xl font-bold text-zinc-100 mb-2">{q.name}</h3>
                    <div className="flex items-center gap-4 text-sm text-zinc-400 mt-4">
                       <span className="flex items-center gap-1.5"><Server size={14}/> {q.concurrency} Concurrency</span>
                       <span className="flex items-center gap-1.5"><Clock size={14}/> Max Retries: 3</span>
                    </div>
                  </div>
                ))}
             </div>
          )}

          {activeTab === 'workers' && (
             <div className="bg-[#121214] border border-zinc-800/80 rounded-2xl shadow-xl shadow-black/20 p-8 text-center animate-in fade-in duration-500">
                <div className="inline-flex p-4 bg-emerald-500/10 rounded-full mb-4">
                  <Server className="text-emerald-500" size={48} />
                </div>
                <h3 className="text-2xl font-bold text-zinc-100 mb-2">Worker Pool Status</h3>
                <p className="text-zinc-400 max-w-md mx-auto mb-8">Workers are currently managed dynamically via Java 21 Virtual Threads inside the execution engine. No standalone workers registered yet.</p>
                <div className="grid grid-cols-3 gap-4 max-w-2xl mx-auto text-left">
                   <div className="bg-zinc-900/50 p-4 rounded-xl border border-zinc-800">
                     <h4 className="text-zinc-500 text-xs uppercase font-bold tracking-wider mb-1">Total Threads</h4>
                     <p className="text-2xl font-semibold text-emerald-400">{metrics.activeWorkers > 0 ? metrics.activeWorkers * 5 : 0}</p>
                   </div>
                   <div className="bg-zinc-900/50 p-4 rounded-xl border border-zinc-800">
                     <h4 className="text-zinc-500 text-xs uppercase font-bold tracking-wider mb-1">CPU Usage</h4>
                     <p className="text-2xl font-semibold text-zinc-100">14%</p>
                   </div>
                   <div className="bg-zinc-900/50 p-4 rounded-xl border border-zinc-800">
                     <h4 className="text-zinc-500 text-xs uppercase font-bold tracking-wider mb-1">Memory</h4>
                     <p className="text-2xl font-semibold text-zinc-100">412 MB</p>
                   </div>
                </div>
             </div>
          )}

          {activeTab === 'dlq' && (
             <div className="bg-[#121214] border border-rose-900/30 rounded-2xl shadow-xl shadow-black/20 overflow-hidden animate-in fade-in duration-500">
               <div className="px-6 py-4 border-b border-zinc-800/80 flex justify-between items-center bg-rose-500/5">
                 <h3 className="font-semibold text-lg text-rose-100 flex items-center gap-2">
                   <AlertCircle size={20} className="text-rose-500"/> Dead Letter Queue
                 </h3>
               </div>
               <div className="p-12 text-center">
                 <XCircle className="text-zinc-700 mx-auto mb-4" size={48} />
                 <h4 className="text-xl font-medium text-zinc-300 mb-2">No failed jobs yet</h4>
                 <p className="text-zinc-500 max-w-sm mx-auto">Jobs that exhaust their retry attempts will appear here for manual intervention.</p>
               </div>
             </div>
          )}

        </main>
      </div>
    </div>
  );
}

function NavItem({ icon, label, active, onClick }) {
  return (
    <button 
      onClick={onClick}
      className={`w-full flex items-center gap-3 px-4 py-3 rounded-xl text-sm font-medium transition-all duration-300 ${
        active 
          ? 'bg-blue-600/10 text-blue-400 border border-blue-500/20 shadow-sm shadow-blue-900/10' 
          : 'text-zinc-400 hover:bg-zinc-800/50 hover:text-zinc-200 border border-transparent'
      }`}
    >
      {icon}
      {label}
    </button>
  );
}

function StatCard({ title, value, icon, trend }) {
  return (
    <div className="bg-[#121214] border border-zinc-800/80 rounded-2xl p-6 shadow-lg shadow-black/20 hover:border-zinc-700 transition-all duration-300 hover:-translate-y-1 group">
      <div className="flex justify-between items-start mb-4">
        <h3 className="text-zinc-400 text-sm font-medium">{title}</h3>
        <div className="p-2.5 bg-zinc-800/50 rounded-xl group-hover:bg-zinc-800 transition-colors shadow-inner">
          {icon}
        </div>
      </div>
      <div className="flex items-baseline gap-2">
        <h2 className="text-3xl font-bold tracking-tight text-white">{value}</h2>
      </div>
      <p className="text-xs text-zinc-500 mt-3 font-medium flex items-center gap-1.5">
        <ArrowRight size={12} className="text-zinc-600"/> {trend}
      </p>
    </div>
  );
}

function QueueItem({ name, pending, workers, status }) {
  return (
    <div className="flex items-center justify-between p-4 rounded-xl bg-zinc-900/40 border border-zinc-800/50 hover:bg-zinc-800/40 transition-colors">
      <div>
        <h4 className="text-sm font-semibold text-zinc-200">{name}</h4>
        <div className="flex items-center gap-4 mt-2">
          <span className="text-xs text-zinc-500 flex items-center gap-1.5"><Clock size={12}/> {pending} pending</span>
          <span className="text-xs text-zinc-500 flex items-center gap-1.5"><Server size={12}/> {workers} workers</span>
        </div>
      </div>
      <div className="flex flex-col items-end gap-2">
         <span className={`text-xs font-semibold px-2 py-0.5 rounded-full ${
           status === 'healthy' ? 'text-emerald-400 bg-emerald-400/10 border border-emerald-400/20' : 
           status === 'warning' ? 'text-amber-400 bg-amber-400/10 border border-amber-400/20' : 'text-rose-400 bg-rose-400/10 border border-rose-400/20'
         }`}>
           {status}
         </span>
      </div>
    </div>
  );
}

function StatusBadge({ status }) {
  const styles = {
    'PENDING': 'text-amber-400 bg-amber-400/10 border-amber-400/20',
    'IN_PROGRESS': 'text-blue-400 bg-blue-400/10 border-blue-400/20',
    'COMPLETED': 'text-emerald-400 bg-emerald-400/10 border-emerald-400/20',
    'FAILED': 'text-rose-400 bg-rose-400/10 border-rose-400/20'
  };
  const badgeStyle = styles[status] || 'text-zinc-400 bg-zinc-400/10 border-zinc-400/20';
  
  return (
    <span className={`text-xs font-semibold px-2.5 py-1 rounded-full border ${badgeStyle}`}>
      {status}
    </span>
  );
}
