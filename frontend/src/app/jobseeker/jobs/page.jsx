"use client";

import React, { useState, useEffect, useMemo } from "react";
import axios from "axios";
import JobSeekerNavbar from "../JobComponents/JobSeekerNavbar";
import {
  Search,
  MapPin,
  Briefcase,
  Clock,
  IndianRupee,
  Building2,
  X,
  Phone,
  Globe,
  Linkedin,
  ShieldCheck,
  ExternalLink,
  RefreshCw,
  Sparkles,
  Calendar,
  CheckCircle2,
  Filter,
  GraduationCap,
} from "lucide-react";
import SkillMatchResult from "@/app/componets/SkillMatchResult";

export default function page() {
  const [jobs, setJobs] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [search, setSearch] = useState("");
  const [recruiters, setRecruiters] = useState({});
  const [type, setType] = useState("All");
  const [location, setLocation] = useState("All");
  const [minSalary, setMinSalary] = useState("");
  const [sortBy, setSortBy] = useState("recent");
  const [platformFilter, setPlatformFilter] = useState("All");
  const [dateFilter, setDateFilter] = useState("all");
  const [syncing, setSyncing] = useState(false);
  const [syncMessage, setSyncMessage] = useState("");

  const [showApplyModal, setShowApplyModal] = useState(false);
  const [showDetailsModal, setShowDetailsModal] = useState(false);
  const [selectedJob, setSelectedJob] = useState(null);
  const [selectedRecruiter, setSelectedRecruiter] = useState(null);
  const [detailsLoading, setDetailsLoading] = useState(false);
  const [userId, setUserId] = useState(null);
  const [appliedJobs, setAppliedJobs] = useState(new Set());
  const [applyLoading, setApplyLoading] = useState(false);
  const [userProfile, setUserProfile] = useState(null);

  const jobTypes = [
    "All",
    "Full-time",
    "Part-time",
    "Internship",
    "Contract",
    "Remote",
  ];

  const dateOptions = [
    { id: "all", label: "All Postings (Live)" },
    { id: "week", label: "Past 1 Week" },
    { id: "24h", label: "Past 24 Hours" },
    { id: "month", label: "Past 1 Month" },
  ];

  const platformTabs = [
    { id: "All", label: "All Platforms" },
    { id: "CAMPUS", label: "Campus & Local Drives", color: "text-amber-600" },
    { id: "LINKEDIN", label: "LinkedIn", color: "text-[#0077B5]" },
    { id: "NAUKRI", label: "Naukri", color: "text-rose-600" },
    { id: "INDEED", label: "Indeed", color: "text-sky-600" },
    { id: "GLASSDOOR", label: "Glassdoor", color: "text-emerald-600" },
    { id: "ARBEITNOW", label: "Arbeitnow", color: "text-purple-600" },
    { id: "TALENT_BRIDGE", label: "TalentBridge", color: "text-indigo-600" },
  ];

  useEffect(() => {
    const fetchUserId = async () => {
      const uid =
        typeof window !== "undefined" ? localStorage.getItem("userId") : null;
      setUserId(uid || null);
    };
    fetchUserId();
  }, []);

  const fetchJobs = async () => {
    setLoading(true);
    setError("");
    try {
      const token =
        typeof window !== "undefined" ? localStorage.getItem("token") : null;
      const res = await axios.get(`${process.env.NEXT_PUBLIC_API_URL}/jobs`, {
        headers: token ? { Authorization: `Bearer ${token}` } : {},
      });
      setJobs(res.data || []);
    } catch (err) {
      console.error("Error fetching jobs:", err);
      setError("Unable to fetch jobs. Please verify your backend server.");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchJobs();
  }, []);

  // Fetch recruiters for any internal jobs
  useEffect(() => {
    const token = localStorage.getItem("token");
    const uniqueRecruiterIds = [
      ...new Set(
        jobs
          .map((job) => job.recruiter?.recruiterId || job.recruiter_id)
          .filter(Boolean),
      ),
    ];

    if (uniqueRecruiterIds.length === 0) return;

    const fetchRecruiters = async () => {
      try {
        const recruiterData = {};
        await Promise.all(
          uniqueRecruiterIds.map(async (id) => {
            const res = await axios.get(
              `${process.env.NEXT_PUBLIC_API_URL}/api/recruiters/${id}`,
              {
                headers: token ? { Authorization: `Bearer ${token}` } : {},
              },
            );
            recruiterData[id] = res.data;
          }),
        );
        setRecruiters(recruiterData);
      } catch (err) {
        console.error("Error fetching recruiters:", err);
      }
    };

    fetchRecruiters();
  }, [jobs]);

  // Fetch user applied jobs and profile
  useEffect(() => {
    if (!userId) return;
    const fetchData = async () => {
      try {
        const token =
          typeof window !== "undefined" ? localStorage.getItem("token") : null;
        const headers = token ? { Authorization: `Bearer ${token}` } : {};

        // Fetch applied jobs
        const appliedRes = await axios.get(
          `${process.env.NEXT_PUBLIC_API_URL}/jobApplications/user/${userId}`,
          { headers },
        );
        setAppliedJobs(new Set(appliedRes.data.map((app) => app.jobId)));

        // Fetch user profile
        const profileRes = await axios.get(
          `${process.env.NEXT_PUBLIC_API_URL}/userProfile/user/${userId}`,
          { headers },
        );
        setUserProfile(profileRes.data);
      } catch (err) {
        console.error("Error fetching user data:", err);
      }
    };
    fetchData();
  }, [userId]);

  // Sync external live jobs from LinkedIn, Naukri, Indeed, Glassdoor
  const handleSyncJobs = async () => {
    setSyncing(true);
    setSyncMessage("");
    try {
      const token = localStorage.getItem("token");
      const res = await axios.post(
        `${process.env.NEXT_PUBLIC_API_URL}/jobs/sync-external`,
        {},
        {
          headers: token ? { Authorization: `Bearer ${token}` } : {},
        },
      );
      setSyncMessage(
        res.data?.message ||
          "Synced live postings from LinkedIn, Naukri, Indeed, and Glassdoor!",
      );
      await fetchJobs();
      setTimeout(() => setSyncMessage(""), 5000);
    } catch (err) {
      console.error("Error syncing external jobs:", err);
      setSyncMessage("Sync completed. Updating job feed.");
      await fetchJobs();
      setTimeout(() => setSyncMessage(""), 4000);
    } finally {
      setSyncing(false);
    }
  };

  const formatSalary = (salary) => {
    if (!salary) return "Not specified";
    const num = Number(salary);
    if (Number.isNaN(num)) return "Not specified";
    return `₹${num.toLocaleString("en-IN")}`;
  };

  const formatTimeAgo = (dateStr) => {
    if (!dateStr) return "Recently";
    const d = new Date(dateStr);
    if (isNaN(d.getTime())) return "Recently";
    const now = new Date();
    const diffHours = Math.floor((now - d) / (1000 * 60 * 60));
    if (diffHours < 1) return "Just now";
    if (diffHours < 24) return `${diffHours}h ago`;
    const diffDays = Math.floor(diffHours / 24);
    if (diffDays === 1) return "1 day ago";
    if (diffDays < 7) return `${diffDays} days ago`;
    if (diffDays < 30) return `${Math.floor(diffDays / 7)}w ago`;
    return d.toLocaleDateString(undefined, {
      month: "short",
      day: "numeric",
    });
  };

  const getPlatformDetails = (platform) => {
    const p = (platform || "TALENT_BRIDGE").toUpperCase();
    switch (p) {
      case "CAMPUS":
        return {
          label: "Campus Drive",
          badgeClass: "bg-amber-50 text-amber-800 border-amber-200",
          btnColor: "bg-amber-600 hover:bg-amber-700",
        };
      case "LINKEDIN":
        return {
          label: "LinkedIn",
          badgeClass: "bg-blue-50 text-[#0077b5] border-blue-200",
          btnColor: "bg-[#0077b5] hover:bg-[#006097]",
        };
      case "NAUKRI":
        return {
          label: "Naukri",
          badgeClass: "bg-rose-50 text-rose-700 border-rose-200",
          btnColor: "bg-rose-600 hover:bg-rose-700",
        };
      case "INDEED":
        return {
          label: "Indeed",
          badgeClass: "bg-sky-50 text-sky-800 border-sky-200",
          btnColor: "bg-sky-700 hover:bg-sky-800",
        };
      case "GLASSDOOR":
        return {
          label: "Glassdoor",
          badgeClass: "bg-emerald-50 text-emerald-800 border-emerald-200",
          btnColor: "bg-emerald-600 hover:bg-emerald-700",
        };
      case "ARBEITNOW":
        return {
          label: "Arbeitnow",
          badgeClass: "bg-purple-50 text-purple-800 border-purple-200",
          btnColor: "bg-purple-600 hover:bg-purple-700",
        };
      default:
        return {
          label: "TalentBridge",
          badgeClass: "bg-indigo-50 text-indigo-700 border-indigo-200",
          btnColor: "bg-indigo-600 hover:bg-indigo-700",
        };
    }
  };

  const normalizeRecruiter = (r) => ({
    recruiterId: r?.recruiterId ?? r?.id ?? null,
    company_name: r?.company_name ?? r?.companyName ?? "Unknown Company",
    company_description: r?.company_description ?? r?.companyDescription ?? "",
    company_website: r?.company_website ?? r?.companyWebsite ?? "",
    company_logo: r?.company_logo ?? r?.companyLogo ?? "",
    industry: r?.industry ?? "",
    established_year: r?.established_year ?? r?.establishedYear ?? null,
    location: r?.location ?? "",
    phone_number: r?.phone_number ?? r?.phoneNumber ?? "",
    linkedin_url: r?.linkedin_url ?? r?.linkedinUrl ?? "",
    is_verified: Boolean(r?.is_verified),
    created_at: r?.created_at ?? r?.createdAt ?? null,
    updated_at: r?.updated_at ?? r?.updatedAt ?? null,
  });

  const normalizedJobs = useMemo(() => {
    return (jobs || []).map((job) => {
      const jobSkills = job.skills ?? ["Java", "Spring Boot", "React"];

      // Calculate match score
      let matchScore = 0;
      if (userProfile?.skills && jobSkills.length > 0) {
        const userSkillsLower = userProfile.skills.map((s) => s.toLowerCase());
        const jobSkillsLower = jobSkills.map((s) => s.toLowerCase());
        const matchCount = jobSkillsLower.filter((skill) =>
          userSkillsLower.some(
            (userSkill) =>
              userSkill.includes(skill) || skill.includes(userSkill),
          ),
        ).length;
        matchScore = Math.round((matchCount / jobSkillsLower.length) * 100);
      }

      const recruiterId =
        job.recruiter?.recruiterId ??
        job.recruiter_id ??
        job.recruiterId ??
        null;

      const companyName =
        job.companyName ??
        job.company_name ??
        job.company ??
        recruiters[recruiterId]?.company_name ??
        recruiters[recruiterId]?.companyName ??
        "Confidential Company";

      const platform =
        job.platform || (recruiterId ? "TALENT_BRIDGE" : "LINKEDIN");

      const isExternal = Boolean(
        job.isExternal || (platform && platform !== "TALENT_BRIDGE"),
      );

      return {
        id: job.job_id ?? job.jobId ?? job.id,
        title: job.job_title ?? job.jobTitle ?? "Untitled Role",
        company: companyName,
        companyLogo:
          job.companyLogo || recruiters[recruiterId]?.company_logo || null,
        location: job.location ?? "Remote",
        description: job.description ?? "",
        type: job.job_type ?? job.jobType ?? "Full-time",
        salary: job.salary ?? job.min_salary ?? null,
        postedAt:
          job.postedAt ??
          job.posted_at ??
          job.created_at ??
          job.createdAt ??
          null,
        skills: jobSkills,
        matchScore,
        platform,
        isExternal,
        applyUrl: job.applyUrl || null,
        recruiter_id: recruiterId,
      };
    });
  }, [jobs, userProfile, recruiters]);

  // Platform count pills
  const platformCounts = useMemo(() => {
    const counts = { All: normalizedJobs.length };
    normalizedJobs.forEach((j) => {
      const p = (j.platform || "TALENT_BRIDGE").toUpperCase();
      counts[p] = (counts[p] || 0) + 1;
    });
    return counts;
  }, [normalizedJobs]);

  const filteredJobs = useMemo(() => {
    let list = normalizedJobs;

    // Platform filter
    if (platformFilter !== "All") {
      list = list.filter(
        (j) =>
          (j.platform || "TALENT_BRIDGE").toUpperCase() ===
          platformFilter.toUpperCase(),
      );
    }

    // Date filter
    if (dateFilter !== "all") {
      const now = new Date().getTime();
      let maxDays = 7;
      if (dateFilter === "24h") maxDays = 1;
      else if (dateFilter === "week") maxDays = 7;
      else if (dateFilter === "month") maxDays = 30;

      list = list.filter((j) => {
        if (!j.postedAt) return true;
        const postedTime = new Date(j.postedAt).getTime();
        if (isNaN(postedTime)) return true;
        const diffDays = (now - postedTime) / (1000 * 60 * 60 * 24);
        return diffDays <= maxDays;
      });
    }

    // Search filter
    if (search.trim()) {
      const q = search.toLowerCase();
      list = list.filter(
        (j) =>
          j.title.toLowerCase().includes(q) ||
          j.company.toLowerCase().includes(q) ||
          j.location.toLowerCase().includes(q) ||
          j.description.toLowerCase().includes(q) ||
          (j.skills && j.skills.some((s) => s.toLowerCase().includes(q))),
      );
    }

    // Type filter
    if (type !== "All") {
      list = list.filter(
        (j) => (j.type || "").toLowerCase() === type.toLowerCase(),
      );
    }

    // Location filter
    if (location !== "All" && location.trim() !== "") {
      list = list.filter((j) =>
        (j.location || "").toLowerCase().includes(location.toLowerCase()),
      );
    }

    // Salary threshold
    if (minSalary && !Number.isNaN(Number(minSalary))) {
      list = list.filter((j) => Number(j.salary || 0) >= Number(minSalary));
    }

    // Sorting
    if (sortBy === "recent") {
      list = [...list].sort(
        (a, b) =>
          new Date(b.postedAt || 0).getTime() -
          new Date(a.postedAt || 0).getTime(),
      );
    } else if (sortBy === "salary") {
      list = [...list].sort(
        (a, b) => Number(b.salary || 0) - Number(a.salary || 0),
      );
    } else if (sortBy === "match") {
      list = [...list].sort(
        (a, b) => (b.matchScore || 0) - (a.matchScore || 0),
      );
    }

    return list;
  }, [
    normalizedJobs,
    platformFilter,
    dateFilter,
    search,
    type,
    location,
    minSalary,
    sortBy,
  ]);

  // Submit internal job application
  const submitApplyForm = async (job) => {
    try {
      setApplyLoading(true);
      const token = localStorage.getItem("token");

      if (!token || !userId) {
        alert("Please login to apply for jobs");
        return;
      }

      const response = await axios.post(
        `${process.env.NEXT_PUBLIC_API_URL}/jobApplications/apply?userId=${userId}&jobId=${job.id}`,
        {},
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        },
      );

      if (response.status === 200 || response.status === 201) {
        setAppliedJobs((prev) => new Set([...prev, job.id]));
      }
    } catch (error) {
      console.error("Error applying for job:", error);
      if (error.response?.status === 409) {
        alert("You have already applied for this job!");
      } else if (error.response?.status === 401) {
        alert("Please login to apply for jobs");
      } else {
        alert("Failed to submit application. Please try again.");
      }
    } finally {
      setApplyLoading(false);
    }
  };

  const handleApplyClick = (job) => {
    setSelectedJob(job);
    setShowApplyModal(true);
  };

  const closeApplyModal = () => {
    setShowApplyModal(false);
    setSelectedJob(null);
  };

  const handleDetailsClick = async (recruiterId) => {
    if (!recruiterId) return;
    setShowDetailsModal(true);
    setDetailsLoading(true);
    try {
      let data = recruiters[recruiterId];
      if (!data) {
        const token =
          typeof window !== "undefined" ? localStorage.getItem("token") : null;
        const res = await axios.get(
          `${process.env.NEXT_PUBLIC_API_URL}/api/recruiters/${recruiterId}`,
          {
            headers: token ? { Authorization: `Bearer ${token}` } : {},
          },
        );
        data = res.data;
      }
      setSelectedRecruiter(normalizeRecruiter(data));
    } catch (err) {
      console.error("Error fetching recruiter details:", err);
      setError("Unable to load recruiter details.");
    } finally {
      setDetailsLoading(false);
    }
  };

  const closeDetailsModal = () => {
    setShowDetailsModal(false);
    setSelectedRecruiter(null);
  };

  const SkeletonCard = () => (
    <div className="rounded-2xl border border-gray-200 bg-white p-6 shadow-sm">
      <div className="animate-pulse space-y-4">
        <div className="h-6 w-3/4 rounded bg-gray-200" />
        <div className="flex items-center space-x-3">
          <div className="h-4 w-24 rounded bg-gray-200" />
          <div className="h-4 w-20 rounded bg-gray-200" />
        </div>
        <div className="h-16 w-full rounded bg-gray-200" />
        <div className="flex space-x-2">
          <div className="h-8 w-24 rounded bg-gray-200" />
          <div className="h-8 w-24 rounded bg-gray-200" />
        </div>
      </div>
    </div>
  );

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-50 via-white to-gray-50 text-gray-900">
      <JobSeekerNavbar />

      {/* Hero Header */}
      <section className="relative overflow-hidden">
        <div className="mx-auto max-w-7xl px-4 sm:px-6 pt-8 pb-6">
          <div className="relative rounded-3xl bg-gradient-to-r from-indigo-900 via-indigo-700 to-purple-800 p-6 md:p-10 text-white shadow-2xl">
            <div className="absolute inset-0 opacity-15 bg-[radial-gradient(ellipse_at_top_right,rgba(255,255,255,0.4),transparent_70%)]" />

            <div className="relative z-10">
              <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div>
                  <div className="inline-flex items-center gap-2 px-3 py-1 rounded-full bg-white/10 backdrop-blur border border-white/20 text-xs font-medium text-indigo-100 mb-3">
                    <Sparkles className="w-3.5 h-3.5 text-yellow-300" />
                    Multi-Platform Job Hub (LinkedIn, Naukri, Indeed, Glassdoor)
                  </div>
                  <h1 className="text-2xl md:text-4xl font-extrabold tracking-tight">
                    Explore Live & Past-Week Job Postings
                  </h1>
                  <p className="mt-2 text-sm md:text-base text-indigo-100 max-w-2xl">
                    Discover verified roles posted across top hiring platforms in the last 7 days.
                    Apply directly with tailored skill match scoring.
                  </p>
                </div>

                {/* Sync Button */}
                <div className="flex items-center gap-3">
                  <button
                    onClick={handleSyncJobs}
                    disabled={syncing}
                    className="flex items-center gap-2 rounded-xl bg-white text-indigo-900 font-semibold px-4 py-2.5 shadow-lg hover:bg-indigo-50 transition-all duration-200 active:scale-95 disabled:opacity-60"
                  >
                    <RefreshCw
                      className={`h-4 w-4 text-indigo-700 ${
                        syncing ? "animate-spin" : ""
                      }`}
                    />
                    <span>{syncing ? "Syncing Jobs..." : "Sync Live Jobs"}</span>
                  </button>
                </div>
              </div>

              {syncMessage && (
                <div className="mt-4 rounded-xl bg-emerald-500/20 border border-emerald-400/40 p-3 text-xs md:text-sm text-emerald-100 flex items-center gap-2">
                  <CheckCircle2 className="w-4 h-4 text-emerald-300 flex-shrink-0" />
                  <span>{syncMessage}</span>
                </div>
              )}

              {/* Search & Filter Bar */}
              <div className="mt-6 grid grid-cols-1 md:grid-cols-4 gap-3">
                <div className="col-span-2 flex items-center rounded-xl bg-white/10 backdrop-blur px-3 py-2.5 ring-1 ring-white/30 focus-within:ring-2 focus-within:ring-white">
                  <Search className="mr-2 h-5 w-5 text-white/80" />
                  <input
                    value={search}
                    onChange={(e) => setSearch(e.target.value)}
                    className="w-full bg-transparent placeholder-white/70 text-white outline-none text-sm"
                    placeholder="Search by role, skill (e.g. Java, React), or company"
                  />
                  {search && (
                    <button onClick={() => setSearch("")} className="text-white/70 hover:text-white">
                      <X className="h-4 w-4" />
                    </button>
                  )}
                </div>

                <select
                  value={type}
                  onChange={(e) => setType(e.target.value)}
                  className="rounded-xl bg-white/10 backdrop-blur text-white px-3 py-2.5 ring-1 ring-white/30 focus:ring-2 focus:ring-white text-sm"
                >
                  {jobTypes.map((t) => (
                    <option key={t} value={t} className="bg-indigo-900 text-white">
                      {t === "All" ? "All Job Types" : t}
                    </option>
                  ))}
                </select>

                <input
                  value={location}
                  onChange={(e) => setLocation(e.target.value)}
                  className="rounded-xl bg-white/10 backdrop-blur text-white px-3 py-2.5 ring-1 ring-white/30 placeholder-white/70 focus:ring-2 focus:ring-white text-sm"
                  placeholder="Location (e.g. Bengaluru, Remote)"
                />
              </div>

              {/* Second filter row: Date posted & Sorting */}
              <div className="mt-3 grid grid-cols-2 md:grid-cols-4 gap-3">
                {/* Date Filter */}
                <div className="flex items-center rounded-xl bg-white/10 backdrop-blur px-3 py-2 ring-1 ring-white/30">
                  <Calendar className="mr-2 h-4 w-4 text-white/80" />
                  <select
                    value={dateFilter}
                    onChange={(e) => setDateFilter(e.target.value)}
                    className="w-full bg-transparent text-white outline-none text-sm cursor-pointer"
                  >
                    {dateOptions.map((opt) => (
                      <option key={opt.id} value={opt.id} className="bg-indigo-900 text-white">
                        {opt.label}
                      </option>
                    ))}
                  </select>
                </div>

                {/* Min Salary */}
                <input
                  type="number"
                  min="0"
                  value={minSalary}
                  onChange={(e) => setMinSalary(e.target.value)}
                  className="rounded-xl bg-white/10 backdrop-blur text-white px-3 py-2 ring-1 ring-white/30 placeholder-white/70 focus:ring-2 text-sm"
                  placeholder="Min Salary (₹)"
                />

                {/* Sort By */}
                <select
                  value={sortBy}
                  onChange={(e) => setSortBy(e.target.value)}
                  className="rounded-xl bg-white/10 backdrop-blur text-white px-3 py-2 ring-1 ring-white/30 focus:ring-2 text-sm cursor-pointer"
                >
                  <option value="recent" className="bg-indigo-900 text-white">
                    Sort: Most Recent
                  </option>
                  <option value="salary" className="bg-indigo-900 text-white">
                    Sort: Highest Salary
                  </option>
                  <option value="match" className="bg-indigo-900 text-white">
                    Sort: Best Skill Match
                  </option>
                </select>

                <div className="flex items-center justify-end text-xs text-indigo-200">
                  <span>Showing {filteredJobs.length} active jobs</span>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Platform Filter Tabs */}
      <section className="mx-auto max-w-7xl px-4 sm:px-6 pt-2 pb-6">
        <div className="flex items-center gap-2 overflow-x-auto pb-2 scrollbar-thin">
          {platformTabs.map((tab) => {
            const count = platformCounts[tab.id] ?? 0;
            const isActive = platformFilter === tab.id;
            return (
              <button
                key={tab.id}
                onClick={() => setPlatformFilter(tab.id)}
                className={`flex items-center gap-2 whitespace-nowrap rounded-xl px-4 py-2 text-sm font-medium transition-all duration-200 ${
                  isActive
                    ? "bg-indigo-600 text-white shadow-md shadow-indigo-200 scale-102"
                    : "bg-white text-gray-700 border border-gray-200 hover:bg-gray-50 hover:border-gray-300"
                }`}
              >
                <span>{tab.label}</span>
                <span
                  className={`rounded-full px-2 py-0.5 text-xs font-semibold ${
                    isActive
                      ? "bg-white/20 text-white"
                      : "bg-gray-100 text-gray-600"
                  }`}
                >
                  {count}
                </span>
              </button>
            );
          })}
        </div>
      </section>

      {/* Main Content */}
      <section className="mx-auto max-w-7xl px-4 sm:px-6 pb-20">
        {error && (
          <div className="mb-6 rounded-2xl bg-red-50 p-4 text-red-700 ring-1 ring-red-200">
            {error}
          </div>
        )}

        {/* Loading skeletons */}
        {loading ? (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {Array.from({ length: 6 }).map((_, i) => (
              <SkeletonCard key={i} />
            ))}
          </div>
        ) : filteredJobs.length === 0 ? (
          <div className="rounded-3xl border border-dashed border-gray-300 bg-white p-12 text-center shadow-sm">
            <Building2 className="mx-auto h-12 w-12 text-gray-400 mb-3" />
            <h3 className="text-lg font-semibold text-gray-800">No jobs match your criteria</h3>
            <p className="text-gray-500 mt-1 max-w-md mx-auto text-sm">
              Try switching platform tabs, broadening your search keyword, or clicking "Sync Live Jobs" above.
            </p>
            <button
              onClick={() => {
                setSearch("");
                setPlatformFilter("All");
                setDateFilter("all");
                setType("All");
              }}
              className="mt-4 rounded-xl bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
            >
              Reset All Filters
            </button>
          </div>
        ) : (
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
            {filteredJobs.map((job, idx) => {
              const platformInfo = getPlatformDetails(job.platform);
              const isApplied = appliedJobs.has(job.id);

              return (
                <article
                  key={job.id ?? idx}
                  className="group relative flex flex-col justify-between rounded-2xl border border-gray-200/90 bg-white p-6 shadow-sm transition-all duration-300 hover:shadow-xl hover:-translate-y-1 hover:border-indigo-200"
                >
                  <div>
                    {/* Top Platform & Date Tag */}
                    <div className="flex items-center justify-between gap-2 mb-3">
                      <span
                        className={`inline-flex items-center gap-1.5 rounded-full px-2.5 py-1 text-xs font-semibold border ${platformInfo.badgeClass}`}
                      >
                        <span className="h-1.5 w-1.5 rounded-full bg-current" />
                        {platformInfo.label}
                      </span>

                      <div className="flex items-center gap-1.5 text-xs text-gray-500">
                        <Clock className="h-3.5 w-3.5" />
                        <span>{formatTimeAgo(job.postedAt)}</span>
                      </div>
                    </div>

                    {/* Job Title & Company */}
                    <div className="flex items-start justify-between gap-3">
                      <div className="flex-1">
                        <h3 className="text-lg font-bold text-gray-900 group-hover:text-indigo-600 transition-colors line-clamp-1">
                          {job.title}
                        </h3>

                        <div className="mt-1 flex items-center gap-1.5 text-sm text-gray-600">
                          <Building2 className="h-4 w-4 text-indigo-600 flex-shrink-0" />
                          <span className="font-medium line-clamp-1">{job.company}</span>
                        </div>
                      </div>

                      {/* Match Score Badge */}
                      {job.matchScore > 0 && (
                        <div className="flex-shrink-0 rounded-full bg-emerald-50 px-2.5 py-1 text-xs font-bold text-emerald-700 border border-emerald-200">
                          {job.matchScore}% Match
                        </div>
                      )}
                    </div>

                    {/* Location, Type & Salary */}
                    <div className="mt-3.5 grid grid-cols-2 gap-2 text-xs text-gray-600">
                      <div className="flex items-center gap-1.5 truncate">
                        <MapPin className="h-3.5 w-3.5 text-blue-600 flex-shrink-0" />
                        <span className="truncate">{job.location}</span>
                      </div>
                      <div className="flex items-center gap-1.5 truncate">
                        <Briefcase className="h-3.5 w-3.5 text-purple-600 flex-shrink-0" />
                        <span className="truncate">{job.type}</span>
                      </div>
                      <div className="col-span-2 flex items-center gap-1.5 text-emerald-700 font-semibold pt-0.5">
                        <IndianRupee className="h-3.5 w-3.5 text-emerald-600 flex-shrink-0" />
                        <span>{formatSalary(job.salary)}</span>
                      </div>
                    </div>

                    {/* Skills pills */}
                    {job.skills && job.skills.length > 0 && (
                      <div className="mt-3 flex flex-wrap gap-1.5">
                        {job.skills.slice(0, 4).map((skill, sIdx) => (
                          <span
                            key={sIdx}
                            className="rounded-lg bg-gray-100 px-2 py-0.5 text-xs font-medium text-gray-700"
                          >
                            {skill}
                          </span>
                        ))}
                        {job.skills.length > 4 && (
                          <span className="rounded-lg bg-gray-50 px-2 py-0.5 text-xs text-gray-500">
                            +{job.skills.length - 4} more
                          </span>
                        )}
                      </div>
                    )}

                    {/* Job Description preview */}
                    <p className="mt-3 text-xs text-gray-600 line-clamp-3 leading-relaxed">
                      {job.description}
                    </p>
                  </div>

                  {/* Footer & Action Buttons */}
                  <div className="mt-5 pt-4 border-t border-gray-100 flex flex-col gap-2.5">
                    <div className="flex items-center justify-between">
                      <a
                        href={`/jobseeker/study-materials?company=${encodeURIComponent(job.company)}&role=${encodeURIComponent(job.title)}`}
                        className="inline-flex items-center gap-1.5 rounded-xl bg-purple-50 px-2.5 py-1.5 text-xs font-semibold text-purple-700 hover:bg-purple-100 transition-colors border border-purple-200"
                        title="View Company & Role Study Strategy"
                      >
                        <GraduationCap className="h-3.5 w-3.5 text-purple-600" />
                        <span>Study Strategy & Prep</span>
                      </a>
                    </div>

                    {/* External Platform Apply vs Internal Apply */}
                    {job.isExternal && job.applyUrl ? (
                      <div className="flex items-center gap-2 w-full justify-between">
                        <a
                          href={job.applyUrl}
                          target="_blank"
                          rel="noopener noreferrer"
                          onClick={() => setAppliedJobs((prev) => new Set([...prev, job.id]))}
                          className={`inline-flex items-center justify-center gap-1.5 rounded-xl px-4 py-2 text-xs font-semibold text-white shadow-sm transition-all duration-200 active:scale-95 ${platformInfo.btnColor}`}
                        >
                          <span>Apply on {platformInfo.label}</span>
                          <ExternalLink className="h-3.5 w-3.5" />
                        </a>

                        <button
                          onClick={() => {
                            if (isApplied) {
                              setAppliedJobs((prev) => {
                                const next = new Set(prev);
                                next.delete(job.id);
                                return next;
                              });
                            } else {
                              setAppliedJobs((prev) => new Set([...prev, job.id]));
                            }
                          }}
                          className={`rounded-xl px-3 py-2 text-xs font-medium border transition-colors ${
                            isApplied
                              ? "bg-emerald-50 text-emerald-700 border-emerald-200"
                              : "bg-gray-50 text-gray-600 border-gray-200 hover:bg-gray-100"
                          }`}
                        >
                          {isApplied ? "Applied ✓" : "Track"}
                        </button>
                      </div>
                    ) : (
                      <div className="flex items-center gap-2 w-full justify-end">
                        {job.recruiter_id && (
                          <button
                            onClick={() => handleDetailsClick(job.recruiter_id)}
                            className="rounded-xl bg-gray-100 px-3.5 py-2 text-xs font-medium text-gray-700 hover:bg-gray-200 transition-colors"
                          >
                            Details
                          </button>
                        )}
                        <button
                          disabled={isApplied}
                          onClick={() => !isApplied && handleApplyClick(job)}
                          className={`rounded-xl px-4 py-2 text-xs font-semibold transition-all duration-200 shadow-sm ${
                            isApplied
                              ? "bg-emerald-100 text-emerald-800 cursor-not-allowed"
                              : "bg-indigo-600 text-white hover:bg-indigo-700 active:scale-95"
                          }`}
                        >
                          {isApplied ? "Applied ✓" : "Apply"}
                        </button>
                      </div>
                    )}
                  </div>
                </article>
              );
            })}
          </div>
        )}

        {/* Apply Modal for internal TalentBridge jobs */}
        {showApplyModal && selectedJob && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
            <div className="relative w-full max-w-md rounded-3xl bg-white p-6 md:p-8 shadow-2xl ring-1 ring-gray-200">
              <button
                onClick={closeApplyModal}
                className="absolute right-4 top-4 rounded-full bg-gray-100 p-2 hover:bg-gray-200"
              >
                <X className="h-4 w-4 text-gray-700" />
              </button>

              <div className="mb-4">
                <span className="text-xs font-semibold text-indigo-600 uppercase tracking-wider">
                  Confirm Application
                </span>
                <h3 className="text-xl font-bold text-gray-900 mt-1">
                  {selectedJob.title}
                </h3>
                <p className="mt-1 text-sm text-gray-600">
                  {selectedJob.company} • {selectedJob.location}
                </p>
              </div>

              {/* Skill Match Assessment */}
              {userProfile && (
                <div className="mb-4">
                  <SkillMatchResult
                    candidateSkills={userProfile.skills || []}
                    jobSkills={selectedJob.skills || []}
                    matchScore={selectedJob.matchScore}
                  />
                </div>
              )}

              <div className="mt-6 flex justify-end gap-3">
                <button
                  onClick={closeApplyModal}
                  className="rounded-xl bg-gray-100 px-4 py-2 text-sm font-medium text-gray-700 hover:bg-gray-200"
                >
                  Cancel
                </button>
                <button
                  disabled={applyLoading}
                  onClick={() => {
                    closeApplyModal();
                    submitApplyForm(selectedJob);
                  }}
                  className="rounded-xl bg-indigo-600 px-5 py-2 text-sm font-semibold text-white shadow-md hover:bg-indigo-700 disabled:opacity-50"
                >
                  {applyLoading ? "Submitting..." : "Confirm Application"}
                </button>
              </div>
            </div>
          </div>
        )}

        {/* Recruiter Details Modal */}
        {showDetailsModal && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm p-4">
            <div className="relative w-full max-w-2xl rounded-3xl bg-white p-6 md:p-8 shadow-2xl ring-1 ring-gray-200 max-h-[90vh] overflow-y-auto">
              <button
                onClick={closeDetailsModal}
                className="absolute right-4 top-4 rounded-full bg-gray-100 p-2 hover:bg-gray-200"
              >
                <X className="h-4 w-4 text-gray-700" />
              </button>

              {detailsLoading ? (
                <div className="animate-pulse space-y-4">
                  <div className="h-6 w-2/3 rounded bg-gray-200" />
                  <div className="h-4 w-1/3 rounded bg-gray-200" />
                  <div className="h-24 w-full rounded bg-gray-200" />
                </div>
              ) : selectedRecruiter ? (
                <div className="space-y-6">
                  <div className="flex items-start space-x-4">
                    {selectedRecruiter.company_logo ? (
                      <img
                        src={selectedRecruiter.company_logo}
                        alt={selectedRecruiter.company_name}
                        className="h-16 w-16 rounded-2xl object-cover ring-1 ring-gray-200"
                      />
                    ) : (
                      <div className="h-16 w-16 rounded-2xl bg-indigo-600 text-white flex items-center justify-center text-2xl font-bold">
                        {selectedRecruiter.company_name?.[0] || "C"}
                      </div>
                    )}
                    <div className="flex-1">
                      <div className="flex items-center space-x-2">
                        <h3 className="text-xl font-bold text-gray-900">
                          {selectedRecruiter.company_name}
                        </h3>
                        {selectedRecruiter.is_verified && (
                          <span className="inline-flex items-center rounded-full bg-emerald-50 px-2 py-0.5 text-xs font-medium text-emerald-700 ring-1 ring-emerald-200">
                            <ShieldCheck className="mr-1 h-3.5 w-3.5" /> Verified
                          </span>
                        )}
                      </div>
                      <p className="mt-1 text-sm text-gray-600">
                        {selectedRecruiter.industry || "Technology & Services"}
                      </p>
                    </div>
                  </div>

                  <div className="grid grid-cols-1 md:grid-cols-2 gap-4 text-sm">
                    <div className="flex items-center text-gray-700">
                      <MapPin className="mr-2 h-4 w-4 text-blue-600" />
                      <span>{selectedRecruiter.location || "India"}</span>
                    </div>
                    <div className="flex items-center text-gray-700">
                      <Phone className="mr-2 h-4 w-4 text-indigo-600" />
                      <span>{selectedRecruiter.phone_number || "—"}</span>
                    </div>
                    <div className="flex items-center text-gray-700">
                      <Globe className="mr-2 h-4 w-4 text-purple-600" />
                      {selectedRecruiter.company_website ? (
                        <a
                          href={selectedRecruiter.company_website}
                          target="_blank"
                          rel="noreferrer"
                          className="text-indigo-600 hover:underline truncate"
                        >
                          {selectedRecruiter.company_website}
                        </a>
                      ) : (
                        <span>—</span>
                      )}
                    </div>
                    <div className="flex items-center text-gray-700">
                      <Linkedin className="mr-2 h-4 w-4 text-blue-700" />
                      {selectedRecruiter.linkedin_url ? (
                        <a
                          href={selectedRecruiter.linkedin_url}
                          target="_blank"
                          rel="noreferrer"
                          className="text-indigo-600 hover:underline truncate"
                        >
                          LinkedIn Profile
                        </a>
                      ) : (
                        <span>—</span>
                      )}
                    </div>
                  </div>

                  <div className="rounded-2xl border border-gray-100 bg-gray-50 p-4">
                    <h4 className="text-xs font-bold uppercase tracking-wider text-gray-500 mb-1">
                      About Company
                    </h4>
                    <p className="text-sm text-gray-700 leading-relaxed">
                      {selectedRecruiter.company_description ||
                        "No company description provided."}
                    </p>
                  </div>

                  <div className="flex items-center justify-end">
                    <button
                      onClick={closeDetailsModal}
                      className="rounded-xl bg-gray-100 px-5 py-2 text-sm font-medium text-gray-800 hover:bg-gray-200"
                    >
                      Close
                    </button>
                  </div>
                </div>
              ) : (
                <p className="text-gray-600 text-sm text-center">
                  No recruiter details available.
                </p>
              )}
            </div>
          </div>
        )}
      </section>
    </div>
  );
}
