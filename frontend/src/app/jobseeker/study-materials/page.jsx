"use client";

import React, { useState, useEffect, Suspense } from "react";
import { useSearchParams } from "next/navigation";
import axios from "axios";
import JobSeekerNavbar from "../JobComponents/JobSeekerNavbar";
import {
  GraduationCap,
  Building2,
  Briefcase,
  Sparkles,
  BookOpen,
  CheckCircle2,
  Clock,
  Layers,
  Search,
  ExternalLink,
  ChevronRight,
  Target,
  Award,
  AlertCircle,
  FileCode,
  FolderGit2,
  HelpCircle,
  Calendar,
  Share2,
  Compass,
  ArrowRight,
  TrendingUp,
  SlidersHorizontal,
  BookmarkCheck,
  MapPin,
  IndianRupee
} from "lucide-react";

function StudyMaterialsContent() {
  const searchParams = useSearchParams();
  const initialCompany = searchParams?.get("company") || "";
  const initialRole = searchParams?.get("role") || "";

  const [activeTab, setActiveTab] = useState(
    initialCompany ? "companies" : initialRole ? "roles" : "companies"
  );

  // Companies state
  const [companies, setCompanies] = useState([]);
  const [selectedCompany, setSelectedCompany] = useState(null);
  const [companySearch, setCompanySearch] = useState(initialCompany);
  const [companyCategoryFilter, setCompanyCategoryFilter] = useState("All");

  // Roles state
  const [roles, setRoles] = useState([]);
  const [selectedRole, setSelectedRole] = useState(null);
  const [roleSearch, setRoleSearch] = useState(initialRole);

  // Custom Plan Generator state
  const [customCompany, setCustomCompany] = useState(initialCompany || "Google");
  const [customRole, setCustomRole] = useState(initialRole || "Software Engineer");
  const [customDuration, setCustomDuration] = useState(4);
  const [customLevel, setCustomLevel] = useState("Intermediate");
  const [generatingPlan, setGeneratingPlan] = useState(false);
  const [generatedPlan, setGeneratedPlan] = useState(null);

  // General loading & error
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");
  const [expandedQuestions, setExpandedQuestions] = useState({});

  useEffect(() => {
    fetchInitialData();
  }, []);

  const fetchInitialData = async () => {
    setLoading(true);
    setError("");
    const apiUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
    try {
      const [compRes, roleRes] = await Promise.all([
        axios.get(`${apiUrl}/api/prep/companies`),
        axios.get(`${apiUrl}/api/prep/roles`)
      ]);

      const compData = compRes.data || [];
      const roleData = roleRes.data || [];

      setCompanies(compData);
      setRoles(roleData);

      // Match initial company from URL or default to first
      if (initialCompany) {
        const found = compData.find(
          (c) =>
            c.companyName.toLowerCase().includes(initialCompany.toLowerCase()) ||
            initialCompany.toLowerCase().includes(c.id)
        );
        setSelectedCompany(found || compData[0] || null);
      } else {
        setSelectedCompany(compData[0] || null);
      }

      // Match initial role from URL or default to first
      if (initialRole) {
        const foundRole = roleData.find(
          (r) =>
            r.roleTitle.toLowerCase().includes(initialRole.toLowerCase()) ||
            initialRole.toLowerCase().includes(r.id)
        );
        setSelectedRole(foundRole || roleData[0] || null);
      } else {
        setSelectedRole(roleData[0] || null);
      }
    } catch (err) {
      console.error("Error loading preparation data:", err);
      setError("Unable to load study materials from backend.");
    } finally {
      setLoading(false);
    }
  };

  const toggleQuestion = (idx) => {
    setExpandedQuestions((prev) => ({
      ...prev,
      [idx]: !prev[idx]
    }));
  };

  const handleGenerateCustomPlan = async (e) => {
    e?.preventDefault();
    setGeneratingPlan(true);
    const apiUrl = process.env.NEXT_PUBLIC_API_URL || "http://localhost:8080";
    try {
      const res = await axios.post(`${apiUrl}/api/prep/custom-plan`, {
        company: customCompany,
        role: customRole,
        durationWeeks: parseInt(customDuration),
        experienceLevel: customLevel
      });
      setGeneratedPlan(res.data);
    } catch (err) {
      console.error("Failed to generate plan:", err);
    } finally {
      setGeneratingPlan(false);
    }
  };

  // Filtered companies
  const filteredCompanies = companies.filter((c) => {
    const matchesSearch =
      c.companyName.toLowerCase().includes(companySearch.toLowerCase()) ||
      c.overview.toLowerCase().includes(companySearch.toLowerCase());
    const matchesCategory =
      companyCategoryFilter === "All" || c.category === companyCategoryFilter;
    return matchesSearch && matchesCategory;
  });

  // Filtered roles
  const filteredRoles = roles.filter((r) => {
    return (
      r.roleTitle.toLowerCase().includes(roleSearch.toLowerCase()) ||
      r.overview.toLowerCase().includes(roleSearch.toLowerCase())
    );
  });

  return (
    <div className="min-h-screen bg-gradient-to-br from-slate-50 via-gray-50 to-indigo-50/30">
      <JobSeekerNavbar />

      {/* Main Container */}
      <main className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-8">
        {/* Hero Header */}
        <div className="relative overflow-hidden rounded-2xl bg-gradient-to-r from-blue-700 via-indigo-700 to-purple-800 text-white p-8 md:p-10 shadow-xl mb-8">
          <div className="relative z-10 max-w-3xl">
            <div className="inline-flex items-center space-x-2 px-3 py-1.5 rounded-full bg-white/10 backdrop-blur-md border border-white/20 text-blue-100 text-xs font-semibold uppercase tracking-wider mb-4">
              <Sparkles className="w-4 h-4 text-amber-300" />
              <span>Interview Readiness & Study Center</span>
            </div>
            <h1 className="text-3xl md:text-5xl font-extrabold tracking-tight leading-tight mb-4">
              Company-Wise & Role-Wise Study Strategies
            </h1>
            <p className="text-blue-100 text-base md:text-lg leading-relaxed mb-6">
              Master the exact interview patterns, coding questions, and evaluation criteria of top product and service tech companies. Build resume-ready projects and follow week-by-week roadmaps.
            </p>

            {/* Quick Metrics */}
            <div className="grid grid-cols-2 sm:grid-cols-4 gap-4 pt-4 border-t border-white/20">
              <div>
                <div className="text-2xl font-bold text-white">15+</div>
                <div className="text-xs text-blue-200">Top & Local Companies</div>
              </div>
              <div>
                <div className="text-2xl font-bold text-white">4+</div>
                <div className="text-xs text-blue-200">Engineering Roles</div>
              </div>
              <div>
                <div className="text-2xl font-bold text-white">100% Free</div>
                <div className="text-xs text-blue-200">Curated Materials</div>
              </div>
              <div>
                <div className="text-2xl font-bold text-white">AI-Powered</div>
                <div className="text-xs text-blue-200">Custom Study Plans</div>
              </div>
            </div>
          </div>
          {/* Subtle background glow */}
          <div className="absolute right-0 top-0 -mt-10 -mr-10 w-96 h-96 bg-purple-500/20 rounded-full blur-3xl pointer-events-none" />
        </div>

        {/* Tab Switcher */}
        <div className="flex border-b border-gray-200 bg-white rounded-xl shadow-sm p-1.5 mb-8 overflow-x-auto">
          <button
            onClick={() => setActiveTab("companies")}
            className={`flex items-center space-x-2 px-5 py-3 rounded-lg font-semibold text-sm transition-all whitespace-nowrap ${
              activeTab === "companies"
                ? "bg-blue-600 text-white shadow-md shadow-blue-500/20"
                : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
            }`}
          >
            <Building2 className="w-4 h-4" />
            <span>Company-Wise Preparation</span>
          </button>

          <button
            onClick={() => setActiveTab("roles")}
            className={`flex items-center space-x-2 px-5 py-3 rounded-lg font-semibold text-sm transition-all whitespace-nowrap ${
              activeTab === "roles"
                ? "bg-blue-600 text-white shadow-md shadow-blue-500/20"
                : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
            }`}
          >
            <Briefcase className="w-4 h-4" />
            <span>Role-Wise Study Roadmaps</span>
          </button>

          <button
            onClick={() => setActiveTab("generator")}
            className={`flex items-center space-x-2 px-5 py-3 rounded-lg font-semibold text-sm transition-all whitespace-nowrap ${
              activeTab === "generator"
                ? "bg-purple-600 text-white shadow-md shadow-purple-500/20"
                : "text-gray-600 hover:text-gray-900 hover:bg-gray-100"
            }`}
          >
            <Sparkles className="w-4 h-4 text-amber-300" />
            <span>AI Custom Plan Generator</span>
          </button>
        </div>

        {/* TAB 1: COMPANY-WISE PREPARATION */}
        {activeTab === "companies" && (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
            {/* Left Sidebar: Company List */}
            <div className="lg:col-span-4 space-y-4">
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
                <div className="relative mb-3">
                  <Search className="w-4 h-4 absolute left-3 top-3 text-gray-400" />
                  <input
                    type="text"
                    placeholder="Search company (e.g. Google, TCS)..."
                    value={companySearch}
                    onChange={(e) => setCompanySearch(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>

                <div className="flex gap-2 overflow-x-auto pb-1 text-xs">
                  {["All", "Local & Campus Recruiter", "IT Services & Global Consulting", "Tier-1 Product / FAANG"].map((cat) => (
                    <button
                      key={cat}
                      onClick={() => setCompanyCategoryFilter(cat)}
                      className={`px-2.5 py-1 rounded-full whitespace-nowrap transition-colors ${
                        companyCategoryFilter === cat
                          ? "bg-blue-100 text-blue-700 font-semibold"
                          : "bg-gray-100 text-gray-600 hover:bg-gray-200"
                      }`}
                    >
                      {cat}
                    </button>
                  ))}
                </div>
              </div>

              {/* Company Selection Cards */}
              <div className="space-y-2.5 max-h-[600px] overflow-y-auto pr-1">
                {filteredCompanies.map((c) => {
                  const isSelected = selectedCompany?.id === c.id;
                  return (
                    <div
                      key={c.id}
                      onClick={() => setSelectedCompany(c)}
                      className={`cursor-pointer p-4 rounded-xl border transition-all ${
                        isSelected
                          ? "bg-white border-blue-500 shadow-md ring-2 ring-blue-500/20"
                          : "bg-white border-gray-200 hover:border-gray-300 hover:shadow-sm"
                      }`}
                    >
                      <div className="flex items-center justify-between mb-1.5">
                        <h3 className="font-bold text-gray-900 text-base">{c.companyName}</h3>
                        <span className="text-[11px] font-semibold px-2 py-0.5 rounded-full bg-slate-100 text-slate-700">
                          {c.difficultyLevel}
                        </span>
                      </div>

                      {/* Package and Location badges */}
                      <div className="flex flex-wrap items-center gap-1.5 mb-2">
                        {c.packageCTC && (
                          <span className="inline-flex items-center text-[11px] font-bold text-emerald-700 bg-emerald-50 border border-emerald-200/80 px-2 py-0.5 rounded-md">
                            <IndianRupee className="w-3 h-3 mr-0.5" />
                            {c.packageCTC}
                          </span>
                        )}
                        {c.location && (
                          <span className="inline-flex items-center text-[10px] text-gray-600 bg-gray-100 px-2 py-0.5 rounded-md">
                            <MapPin className="w-2.5 h-2.5 mr-0.5 text-gray-400" />
                            {c.location}
                          </span>
                        )}
                      </div>

                      <p className="text-xs text-gray-500 line-clamp-2 mb-2">{c.overview}</p>
                      <div className="flex items-center justify-between text-xs text-blue-600 font-medium">
                        <span>{c.rounds?.length || 4} Rounds Breakdown</span>
                        <ChevronRight className="w-3.5 h-3.5" />
                      </div>
                    </div>
                  );
                })}

                {filteredCompanies.length === 0 && (
                  <div className="text-center py-8 bg-white rounded-xl border border-dashed border-gray-200 text-gray-500 text-sm">
                    No companies matched your query.
                  </div>
                )}
              </div>
            </div>

            {/* Right Panel: Selected Company Details */}
            <div className="lg:col-span-8">
              {selectedCompany ? (
                <div className="space-y-6">
                  {/* Company Overview Header */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-gray-100 pb-6 mb-6">
                      <div>
                        <div className="flex flex-wrap items-center gap-2 mb-2">
                          <h2 className="text-2xl md:text-3xl font-extrabold text-gray-900">
                            {selectedCompany.companyName}
                          </h2>
                          <span className="px-3 py-1 rounded-full text-xs font-semibold bg-blue-50 text-blue-700 border border-blue-100">
                            {selectedCompany.category}
                          </span>
                        </div>
                        <div className="flex flex-wrap items-center gap-2 text-xs text-gray-600">
                          {selectedCompany.packageCTC && (
                            <span className="inline-flex items-center text-emerald-700 font-bold bg-emerald-50 px-2.5 py-1 rounded-md border border-emerald-200">
                              <IndianRupee className="w-3.5 h-3.5 mr-1" />
                              <span>CTC: {selectedCompany.packageCTC}</span>
                            </span>
                          )}
                          {selectedCompany.location && (
                            <span className="inline-flex items-center bg-gray-100 px-2.5 py-1 rounded-md text-gray-700">
                              <MapPin className="w-3.5 h-3.5 mr-1 text-blue-600" />
                              <span>{selectedCompany.location}</span>
                            </span>
                          )}
                          <span className="bg-slate-100 px-2.5 py-1 rounded-md text-slate-700 font-medium">
                            Difficulty: {selectedCompany.difficultyLevel}
                          </span>
                        </div>
                      </div>

                      <button
                        onClick={() => {
                          setCustomCompany(selectedCompany.companyName);
                          setActiveTab("generator");
                        }}
                        className="inline-flex items-center space-x-2 px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg text-sm font-semibold hover:shadow-lg transition-all"
                      >
                        <Sparkles className="w-4 h-4 text-amber-300" />
                        <span>Generate AI Study Plan</span>
                      </button>
                    </div>

                    <p className="text-gray-700 text-sm md:text-base leading-relaxed mb-6">
                      {selectedCompany.overview}
                    </p>

                    {/* Hiring Philosophy */}
                    <div className="bg-amber-50/70 border-l-4 border-amber-500 p-4 rounded-r-xl mb-6">
                      <div className="flex items-center space-x-2 text-amber-800 font-bold text-sm mb-1">
                        <Target className="w-4 h-4" />
                        <span>Hiring Philosophy & What They Value</span>
                      </div>
                      <p className="text-xs md:text-sm text-amber-900/90 leading-relaxed">
                        {selectedCompany.hiringPhilosophy}
                      </p>
                    </div>

                    {/* Key Focus Areas */}
                    <div>
                      <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2.5">
                        Key Evaluation Pillars
                      </h4>
                      <div className="flex flex-wrap gap-2">
                        {selectedCompany.keyFocusAreas?.map((area, idx) => (
                          <span
                            key={idx}
                            className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-gray-100 text-gray-800 text-xs font-medium"
                          >
                            <BookmarkCheck className="w-3.5 h-3.5 text-blue-600" />
                            <span>{area}</span>
                          </span>
                        ))}
                      </div>
                    </div>
                  </div>

                  {/* Interview Rounds Section */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex items-center space-x-2 mb-6">
                      <Layers className="w-5 h-5 text-blue-600" />
                      <h3 className="text-xl font-bold text-gray-900">
                        Hiring Process & Interview Rounds Breakdown
                      </h3>
                    </div>

                    <div className="space-y-4">
                      {selectedCompany.rounds?.map((round, idx) => (
                        <div
                          key={idx}
                          className="p-5 rounded-xl border border-gray-200/90 hover:border-blue-300 bg-slate-50/50 transition-all"
                        >
                          <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 mb-2">
                            <div className="flex items-center space-x-3">
                              <span className="w-7 h-7 rounded-full bg-blue-600 text-white font-bold text-xs flex items-center justify-center shrink-0">
                                {round.roundNumber || idx + 1}
                              </span>
                              <h4 className="font-bold text-gray-900 text-base">
                                {round.roundTitle}
                              </h4>
                            </div>
                            <span className="inline-flex items-center space-x-1 text-xs text-gray-500 font-medium bg-white px-2.5 py-1 rounded-md border border-gray-200">
                              <Clock className="w-3.5 h-3.5 text-gray-400" />
                              <span>{round.format}</span>
                            </span>
                          </div>

                          <p className="text-sm text-gray-700 ml-10 mb-3">
                            <span className="font-semibold text-gray-900">Evaluation Focus: </span>
                            {round.evaluationFocus}
                          </p>

                          {round.tipsForSuccess && round.tipsForSuccess.length > 0 && (
                            <div className="ml-10 bg-white p-3 rounded-lg border border-gray-100 space-y-1">
                              <span className="text-xs font-bold text-blue-700 uppercase tracking-wider block mb-1">
                                Strategy Tips for This Round:
                              </span>
                              {round.tipsForSuccess.map((tip, tIdx) => (
                                <div key={tIdx} className="flex items-start space-x-2 text-xs text-gray-600">
                                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-500 shrink-0 mt-0.5" />
                                  <span>{tip}</span>
                                </div>
                              ))}
                            </div>
                          )}
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Frequently Asked Questions & Coding Patterns */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex items-center space-x-2 mb-6">
                      <HelpCircle className="w-5 h-5 text-indigo-600" />
                      <h3 className="text-xl font-bold text-gray-900">
                        High-Frequency Interview Questions & Solution Blueprints
                      </h3>
                    </div>

                    <div className="space-y-3">
                      {selectedCompany.frequentQuestions?.map((q, idx) => {
                        const isExpanded = expandedQuestions[`comp_${idx}`];
                        return (
                          <div
                            key={idx}
                            className="rounded-xl border border-gray-200 overflow-hidden bg-white transition-all"
                          >
                            <div
                              onClick={() => toggleQuestion(`comp_${idx}`)}
                              className="p-4 cursor-pointer hover:bg-gray-50 flex items-center justify-between gap-4"
                            >
                              <div className="space-y-1">
                                <div className="flex items-center space-x-2">
                                  <span
                                    className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                                      q.difficulty === "Easy"
                                        ? "bg-emerald-100 text-emerald-800"
                                        : q.difficulty === "Medium"
                                        ? "bg-amber-100 text-amber-800"
                                        : "bg-rose-100 text-rose-800"
                                    }`}
                                  >
                                    {q.difficulty}
                                  </span>
                                  <span className="text-xs text-gray-500 font-medium">
                                    {q.topic} • Frequency: <span className="text-blue-600 font-semibold">{q.frequency}</span>
                                  </span>
                                </div>
                                <h4 className="font-semibold text-gray-900 text-sm md:text-base">
                                  {q.question}
                                </h4>
                              </div>
                              <ChevronRight
                                className={`w-5 h-5 text-gray-400 transition-transform ${
                                  isExpanded ? "rotate-90 text-blue-600" : ""
                                }`}
                              />
                            </div>

                            {isExpanded && (
                              <div className="p-4 bg-slate-50 border-t border-gray-100 space-y-3">
                                <div>
                                  <span className="text-xs font-bold text-gray-600 uppercase tracking-wider block mb-1">
                                    Solution Strategy / Blueprint:
                                  </span>
                                  <p className="text-sm text-gray-800 leading-relaxed font-mono bg-white p-3 rounded-lg border border-gray-200">
                                    {q.solutionOutline}
                                  </p>
                                </div>

                                {q.keyPoints && (
                                  <div className="space-y-1">
                                    <span className="text-xs font-bold text-gray-500 uppercase tracking-wider">
                                      Key Evaluation Points:
                                    </span>
                                    {q.keyPoints.map((kp, kIdx) => (
                                      <div key={kIdx} className="flex items-center space-x-2 text-xs text-gray-700">
                                        <div className="w-1.5 h-1.5 rounded-full bg-blue-500" />
                                        <span>{kp}</span>
                                      </div>
                                    ))}
                                  </div>
                                )}

                                {q.practiceUrl && (
                                  <div className="pt-2">
                                    <a
                                      href={q.practiceUrl}
                                      target="_blank"
                                      rel="noreferrer"
                                      className="inline-flex items-center space-x-1.5 text-xs text-blue-600 font-semibold hover:underline"
                                    >
                                      <span>Practice on LeetCode / GitHub</span>
                                      <ExternalLink className="w-3.5 h-3.5" />
                                    </a>
                                  </div>
                                )}
                              </div>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  </div>

                  {/* 4-Week Study Strategy Timeline */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex items-center space-x-2 mb-6">
                      <Calendar className="w-5 h-5 text-purple-600" />
                      <h3 className="text-xl font-bold text-gray-900">
                        Recommended 4-Week Preparation Timeline
                      </h3>
                    </div>

                    <div className="relative pl-6 border-l-2 border-indigo-200 space-y-6">
                      {selectedCompany.studyTimeline?.map((item, idx) => (
                        <div key={idx} className="relative">
                          <div className="absolute -left-[31px] top-0.5 w-4 h-4 rounded-full bg-indigo-600 border-2 border-white shadow-sm" />
                          <div className="bg-indigo-50/50 p-4 rounded-xl border border-indigo-100">
                            <span className="text-xs font-extrabold text-indigo-700 uppercase tracking-wider block mb-1">
                              Milestone {idx + 1}
                            </span>
                            <p className="text-sm font-semibold text-gray-900">{item}</p>
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Curated Study Materials & Links */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex items-center space-x-2 mb-6">
                      <BookOpen className="w-5 h-5 text-emerald-600" />
                      <h3 className="text-xl font-bold text-gray-900">
                        Curated Study Material & Direct Resource Links
                      </h3>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      {selectedCompany.curatedMaterials?.map((mat, idx) => (
                        <a
                          key={idx}
                          href={mat.url}
                          target="_blank"
                          rel="noreferrer"
                          className="p-4 rounded-xl border border-gray-200 hover:border-emerald-500 hover:shadow-md transition-all group bg-white flex flex-col justify-between"
                        >
                          <div>
                            <div className="flex items-center justify-between mb-2">
                              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-100">
                                {mat.type}
                              </span>
                              <span className="text-[10px] font-bold text-emerald-600 bg-emerald-100/60 px-2 py-0.5 rounded-full">
                                {mat.tag}
                              </span>
                            </div>
                            <h4 className="font-bold text-gray-900 text-sm group-hover:text-emerald-600 transition-colors mb-1">
                              {mat.title}
                            </h4>
                            <p className="text-xs text-gray-500 line-clamp-2 mb-3">
                              {mat.description}
                            </p>
                          </div>
                          <div className="flex items-center space-x-1 text-xs text-emerald-600 font-semibold group-hover:underline">
                            <span>Open Resource</span>
                            <ExternalLink className="w-3.5 h-3.5" />
                          </div>
                        </a>
                      ))}
                    </div>
                  </div>
                </div>
              ) : (
                <div className="bg-white p-12 text-center rounded-2xl border border-gray-200 text-gray-500">
                  Select a company from the left to view its comprehensive preparation strategy.
                </div>
              )}
            </div>
          </div>
        )}

        {/* TAB 2: ROLE-WISE PREPARATION */}
        {activeTab === "roles" && (
          <div className="grid grid-cols-1 lg:grid-cols-12 gap-8">
            {/* Left Sidebar: Roles List */}
            <div className="lg:col-span-4 space-y-4">
              <div className="bg-white rounded-xl shadow-sm border border-gray-200 p-4">
                <div className="relative mb-2">
                  <Search className="w-4 h-4 absolute left-3 top-3 text-gray-400" />
                  <input
                    type="text"
                    placeholder="Search roles (e.g. Frontend, Backend)..."
                    value={roleSearch}
                    onChange={(e) => setRoleSearch(e.target.value)}
                    className="w-full pl-9 pr-3 py-2 text-sm border border-gray-200 rounded-lg focus:outline-none focus:ring-2 focus:ring-blue-500"
                  />
                </div>
              </div>

              <div className="space-y-2.5 max-h-[600px] overflow-y-auto pr-1">
                {filteredRoles.map((r) => {
                  const isSelected = selectedRole?.id === r.id;
                  return (
                    <div
                      key={r.id}
                      onClick={() => setSelectedRole(r)}
                      className={`cursor-pointer p-4 rounded-xl border transition-all ${
                        isSelected
                          ? "bg-white border-blue-500 shadow-md ring-2 ring-blue-500/20"
                          : "bg-white border-gray-200 hover:border-gray-300 hover:shadow-sm"
                      }`}
                    >
                      <div className="flex items-center justify-between mb-1">
                        <h3 className="font-bold text-gray-900 text-base">{r.roleTitle}</h3>
                      </div>
                      <span className="text-[11px] font-semibold text-blue-600 block mb-2">
                        {r.category}
                      </span>
                      <p className="text-xs text-gray-500 line-clamp-2 mb-2">{r.overview}</p>
                      <div className="flex items-center justify-between text-xs text-blue-600 font-medium">
                        <span>{r.learningRoadmap?.length || 4} Roadmap Phases</span>
                        <ChevronRight className="w-3.5 h-3.5" />
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>

            {/* Right Panel: Selected Role Details */}
            <div className="lg:col-span-8">
              {selectedRole ? (
                <div className="space-y-6">
                  {/* Role Overview */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4 border-b border-gray-100 pb-6 mb-6">
                      <div>
                        <div className="flex items-center space-x-3 mb-1">
                          <h2 className="text-2xl md:text-3xl font-extrabold text-gray-900">
                            {selectedRole.roleTitle}
                          </h2>
                          <span className="px-3 py-1 rounded-full text-xs font-semibold bg-indigo-50 text-indigo-700 border border-indigo-100">
                            {selectedRole.category}
                          </span>
                        </div>
                        <p className="text-sm text-gray-500">
                          Target Level: <span className="font-semibold text-gray-800">{selectedRole.experienceLevel}</span>
                        </p>
                      </div>

                      <button
                        onClick={() => {
                          setCustomRole(selectedRole.roleTitle);
                          setActiveTab("generator");
                        }}
                        className="inline-flex items-center space-x-2 px-4 py-2 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-lg text-sm font-semibold hover:shadow-lg transition-all"
                      >
                        <Sparkles className="w-4 h-4 text-amber-300" />
                        <span>Generate Custom Study Plan</span>
                      </button>
                    </div>

                    <p className="text-gray-700 text-sm md:text-base leading-relaxed mb-6">
                      {selectedRole.overview}
                    </p>

                    {/* Core Competencies */}
                    <div className="mb-6">
                      <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2.5">
                        Core Competencies to Master
                      </h4>
                      <div className="flex flex-wrap gap-2">
                        {selectedRole.coreCompetencies?.map((comp, idx) => (
                          <span
                            key={idx}
                            className="inline-flex items-center space-x-1.5 px-3 py-1.5 rounded-lg bg-blue-50 text-blue-800 text-xs font-medium border border-blue-100"
                          >
                            <CheckCircle2 className="w-3.5 h-3.5 text-blue-600" />
                            <span>{comp}</span>
                          </span>
                        ))}
                      </div>
                    </div>

                    {/* Core Topics Checklist */}
                    <div>
                      <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2.5">
                        High-Priority Technical Topics
                      </h4>
                      <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                        {selectedRole.coreTopicsToMaster?.map((topic, idx) => (
                          <div
                            key={idx}
                            className="flex items-center space-x-2 text-xs text-gray-700 bg-gray-50 p-2.5 rounded-lg"
                          >
                            <div className="w-1.5 h-1.5 rounded-full bg-purple-600" />
                            <span>{topic}</span>
                          </div>
                        ))}
                      </div>
                    </div>
                  </div>

                  {/* Stage-by-Stage Roadmap */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex items-center space-x-2 mb-6">
                      <Compass className="w-5 h-5 text-indigo-600" />
                      <h3 className="text-xl font-bold text-gray-900">
                        Phase-by-Phase Learning Roadmap
                      </h3>
                    </div>

                    <div className="space-y-4">
                      {selectedRole.learningRoadmap?.map((phase, idx) => (
                        <div
                          key={idx}
                          className="p-5 rounded-xl border border-gray-200 bg-slate-50/50 hover:bg-white hover:border-indigo-300 transition-all"
                        >
                          <div className="flex items-center justify-between mb-2">
                            <span className="text-xs font-extrabold px-2.5 py-1 rounded-md bg-indigo-100 text-indigo-800">
                              {phase.stage}
                            </span>
                            <h4 className="font-bold text-gray-900 text-base">{phase.title}</h4>
                          </div>
                          <p className="text-sm text-gray-600 mb-3">{phase.description}</p>
                          <div className="flex flex-wrap gap-1.5">
                            {phase.topics?.map((topic, tIdx) => (
                              <span
                                key={tIdx}
                                className="text-[11px] px-2 py-0.5 rounded bg-white border border-gray-200 text-gray-700 font-mono"
                              >
                                {topic}
                              </span>
                            ))}
                          </div>
                        </div>
                      ))}
                    </div>
                  </div>

                  {/* Top Role Technical Interview Questions */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex items-center space-x-2 mb-6">
                      <FileCode className="w-5 h-5 text-blue-600" />
                      <h3 className="text-xl font-bold text-gray-900">
                        Top Technical Questions for This Role
                      </h3>
                    </div>

                    <div className="space-y-3">
                      {selectedRole.topInterviewQuestions?.map((q, idx) => {
                        const isExpanded = expandedQuestions[`role_${idx}`];
                        return (
                          <div
                            key={idx}
                            className="rounded-xl border border-gray-200 overflow-hidden bg-white transition-all"
                          >
                            <div
                              onClick={() => toggleQuestion(`role_${idx}`)}
                              className="p-4 cursor-pointer hover:bg-gray-50 flex items-center justify-between gap-4"
                            >
                              <div>
                                <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-blue-50 text-blue-700 mr-2">
                                  {q.difficulty}
                                </span>
                                <span className="text-xs text-gray-500 font-medium mr-2">
                                  {q.topic}
                                </span>
                                <h4 className="font-semibold text-gray-900 text-sm md:text-base mt-1">
                                  {q.question}
                                </h4>
                              </div>
                              <ChevronRight
                                className={`w-5 h-5 text-gray-400 transition-transform ${
                                  isExpanded ? "rotate-90 text-blue-600" : ""
                                }`}
                              />
                            </div>

                            {isExpanded && (
                              <div className="p-4 bg-slate-50 border-t border-gray-100 space-y-3">
                                <div>
                                  <span className="text-xs font-bold text-gray-600 uppercase tracking-wider block mb-1">
                                    Explanation & Key Concept:
                                  </span>
                                  <p className="text-sm text-gray-800 leading-relaxed font-mono bg-white p-3 rounded-lg border border-gray-200">
                                    {q.solutionOutline}
                                  </p>
                                </div>
                                {q.keyPoints && (
                                  <div className="space-y-1">
                                    {q.keyPoints.map((kp, kIdx) => (
                                      <div key={kIdx} className="flex items-center space-x-2 text-xs text-gray-700">
                                        <div className="w-1.5 h-1.5 rounded-full bg-blue-500" />
                                        <span>{kp}</span>
                                      </div>
                                    ))}
                                  </div>
                                )}
                              </div>
                            )}
                          </div>
                        );
                      })}
                    </div>
                  </div>

                  {/* Resume-Worthy Portfolio Project Ideas */}
                  {selectedRole.portfolioProjects && (
                    <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                      <div className="flex items-center space-x-2 mb-6">
                        <FolderGit2 className="w-5 h-5 text-amber-600" />
                        <h3 className="text-xl font-bold text-gray-900">
                          Resume-Worthy Portfolio Project Ideas
                        </h3>
                      </div>

                      <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                        {selectedRole.portfolioProjects.map((proj, idx) => (
                          <div
                            key={idx}
                            className="p-5 rounded-xl border border-gray-200 bg-amber-50/20 flex flex-col justify-between"
                          >
                            <div>
                              <h4 className="font-bold text-gray-900 text-base mb-1">
                                {proj.title}
                              </h4>
                              <p className="text-xs text-gray-600 leading-relaxed mb-3">
                                {proj.description}
                              </p>
                              <div className="text-xs font-mono bg-white p-2 rounded border border-amber-200/60 text-amber-900 mb-3">
                                <span className="font-bold">Tech Stack: </span>{proj.techStack}
                              </div>
                            </div>
                            <div className="space-y-1">
                              <span className="text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                                Impressive Highlights:
                              </span>
                              {proj.keyFeatures?.map((feat, fIdx) => (
                                <div key={fIdx} className="flex items-center space-x-1.5 text-xs text-gray-700">
                                  <CheckCircle2 className="w-3.5 h-3.5 text-emerald-600 shrink-0" />
                                  <span>{feat}</span>
                                </div>
                              ))}
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>
                  )}

                  {/* Curated Role Study Materials */}
                  <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                    <div className="flex items-center space-x-2 mb-6">
                      <BookOpen className="w-5 h-5 text-emerald-600" />
                      <h3 className="text-xl font-bold text-gray-900">
                        Curated Learning Materials & Cheatsheets
                      </h3>
                    </div>

                    <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
                      {selectedRole.curatedMaterials?.map((mat, idx) => (
                        <a
                          key={idx}
                          href={mat.url}
                          target="_blank"
                          rel="noreferrer"
                          className="p-4 rounded-xl border border-gray-200 hover:border-emerald-500 hover:shadow-md transition-all group bg-white flex flex-col justify-between"
                        >
                          <div>
                            <div className="flex items-center justify-between mb-2">
                              <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-emerald-50 text-emerald-700 border border-emerald-100">
                                {mat.type}
                              </span>
                              <span className="text-[10px] font-bold text-emerald-600 bg-emerald-100/60 px-2 py-0.5 rounded-full">
                                {mat.tag}
                              </span>
                            </div>
                            <h4 className="font-bold text-gray-900 text-sm group-hover:text-emerald-600 transition-colors mb-1">
                              {mat.title}
                            </h4>
                            <p className="text-xs text-gray-500 line-clamp-2 mb-3">
                              {mat.description}
                            </p>
                          </div>
                          <div className="flex items-center space-x-1 text-xs text-emerald-600 font-semibold group-hover:underline">
                            <span>Open Guide</span>
                            <ExternalLink className="w-3.5 h-3.5" />
                          </div>
                        </a>
                      ))}
                    </div>
                  </div>
                </div>
              ) : (
                <div className="bg-white p-12 text-center rounded-2xl border border-gray-200 text-gray-500">
                  Select a role from the left to view its roadmap and study materials.
                </div>
              )}
            </div>
          </div>
        )}

        {/* TAB 3: AI CUSTOM STUDY STRATEGY GENERATOR */}
        {activeTab === "generator" && (
          <div className="space-y-8">
            {/* Input Form Card */}
            <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
              <div className="flex items-center space-x-3 mb-6">
                <div className="p-2.5 rounded-xl bg-purple-100 text-purple-700">
                  <Sparkles className="w-6 h-6 text-purple-600" />
                </div>
                <div>
                  <h3 className="text-xl md:text-2xl font-bold text-gray-900">
                    AI Custom Study Strategy & Schedule Generator
                  </h3>
                  <p className="text-xs md:text-sm text-gray-500">
                    Specify your target company and engineering role to generate a day-by-day customized preparation roadmap.
                  </p>
                </div>
              </div>

              <form onSubmit={handleGenerateCustomPlan} className="grid grid-cols-1 md:grid-cols-4 gap-4">
                <div>
                  <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-2">
                    Target Company
                  </label>
                  <input
                    type="text"
                    value={customCompany}
                    onChange={(e) => setCustomCompany(e.target.value)}
                    placeholder="e.g. Google, Amazon, TCS, Uber..."
                    className="w-full px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-2">
                    Engineering Role
                  </label>
                  <input
                    type="text"
                    value={customRole}
                    onChange={(e) => setCustomRole(e.target.value)}
                    placeholder="e.g. Software Engineer, Frontend..."
                    className="w-full px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500"
                    required
                  />
                </div>

                <div>
                  <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-2">
                    Preparation Timeframe
                  </label>
                  <select
                    value={customDuration}
                    onChange={(e) => setCustomDuration(e.target.value)}
                    className="w-full px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 bg-white"
                  >
                    <option value={2}>2 Weeks (Crash Sprint)</option>
                    <option value={4}>4 Weeks (Standard Preparation)</option>
                    <option value={8}>8 Weeks (Comprehensive Mastery)</option>
                  </select>
                </div>

                <div>
                  <label className="block text-xs font-bold text-gray-700 uppercase tracking-wider mb-2">
                    Experience Level
                  </label>
                  <select
                    value={customLevel}
                    onChange={(e) => setCustomLevel(e.target.value)}
                    className="w-full px-4 py-2.5 text-sm border border-gray-200 rounded-xl focus:outline-none focus:ring-2 focus:ring-purple-500 bg-white"
                  >
                    <option value="Fresher / Entry-Level">Fresher / Entry-Level</option>
                    <option value="Intermediate">Intermediate (1-3 Years)</option>
                    <option value="Senior / Lead">Senior / Lead (4+ Years)</option>
                  </select>
                </div>

                <div className="md:col-span-4 pt-2">
                  <button
                    type="submit"
                    disabled={generatingPlan}
                    className="w-full sm:w-auto px-8 py-3.5 bg-gradient-to-r from-purple-600 to-indigo-600 text-white rounded-xl font-bold text-sm shadow-md hover:shadow-lg hover:from-purple-700 hover:to-indigo-700 transition-all flex items-center justify-center space-x-2"
                  >
                    {generatingPlan ? (
                      <>
                        <div className="w-4 h-4 border-2 border-white border-t-transparent rounded-full animate-spin" />
                        <span>Synthesizing Tailored Study Strategy...</span>
                      </>
                    ) : (
                      <>
                        <Sparkles className="w-4 h-4 text-amber-300" />
                        <span>Generate Personalized Study Strategy</span>
                      </>
                    )}
                  </button>
                </div>
              </form>
            </div>

            {/* Generated Plan Output */}
            {generatedPlan && (
              <div className="space-y-6">
                {/* Summary Card */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                  <div className="flex items-center space-x-2 text-purple-600 font-bold text-sm mb-2">
                    <Award className="w-4 h-4" />
                    <span>AI-Generated Preparation Blueprint</span>
                  </div>
                  <h3 className="text-2xl font-extrabold text-gray-900 mb-3">
                    {generatedPlan.durationWeeks}-Week Plan for {generatedPlan.targetRole} at {generatedPlan.targetCompany}
                  </h3>
                  <p className="text-gray-700 text-sm leading-relaxed mb-6">
                    {generatedPlan.planSummary}
                  </p>

                  <div className="mb-4">
                    <h4 className="text-xs font-bold text-gray-400 uppercase tracking-wider mb-2">
                      Priority Skills Checklist
                    </h4>
                    <div className="flex flex-wrap gap-2">
                      {generatedPlan.prioritySkillsToLearn?.map((skill, idx) => (
                        <span
                          key={idx}
                          className="px-3 py-1 rounded-lg bg-purple-50 text-purple-800 text-xs font-semibold border border-purple-100"
                        >
                          ✓ {skill}
                        </span>
                      ))}
                    </div>
                  </div>
                </div>

                {/* Week-by-Week Breakdown */}
                <div className="bg-white rounded-2xl shadow-sm border border-gray-200 p-6 md:p-8">
                  <h3 className="text-xl font-bold text-gray-900 mb-6 flex items-center space-x-2">
                    <Calendar className="w-5 h-5 text-purple-600" />
                    <span>Week-by-Week Study Plan & Milestones</span>
                  </h3>

                  <div className="space-y-4">
                    {generatedPlan.weeklyPlan?.map((week, idx) => (
                      <div
                        key={idx}
                        className="p-5 rounded-xl border border-gray-200 bg-slate-50/50 hover:bg-white transition-all space-y-3"
                      >
                        <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-2 border-b border-gray-100 pb-2">
                          <span className="font-extrabold text-sm text-purple-700 uppercase tracking-wider">
                            Week {week.weekNumber}: {week.theme}
                          </span>
                          <span className="text-xs font-medium text-emerald-700 bg-emerald-50 px-2.5 py-1 rounded-md border border-emerald-100">
                            🎯 Goal: {week.mockTestGoal}
                          </span>
                        </div>

                        <div>
                          <span className="text-xs font-bold text-gray-500 uppercase tracking-wider block mb-1">
                            Focus Topics:
                          </span>
                          <div className="flex flex-wrap gap-1.5 mb-2">
                            {week.focusTopics?.map((top, tIdx) => (
                              <span
                                key={tIdx}
                                className="text-xs px-2 py-0.5 rounded bg-white border border-gray-200 text-gray-700 font-mono"
                              >
                                {top}
                              </span>
                            ))}
                          </div>
                        </div>

                        <div className="space-y-1.5 pt-1">
                          <span className="text-xs font-bold text-gray-500 uppercase tracking-wider block">
                            Daily Action Items:
                          </span>
                          {week.dailyTasks?.map((task, dIdx) => (
                            <div key={dIdx} className="flex items-start space-x-2 text-xs text-gray-700">
                              <CheckCircle2 className="w-3.5 h-3.5 text-purple-500 shrink-0 mt-0.5" />
                              <span>{task}</span>
                            </div>
                          ))}
                        </div>
                      </div>
                    ))}
                  </div>
                </div>

                {/* Pro Tips */}
                {generatedPlan.proTips && (
                  <div className="bg-gradient-to-br from-indigo-900 to-purple-900 text-white rounded-2xl p-6 md:p-8 shadow-md">
                    <h4 className="text-lg font-bold mb-4 flex items-center space-x-2 text-amber-300">
                      <Sparkles className="w-5 h-5" />
                      <span>Insider Interview Tips for {generatedPlan.targetCompany}</span>
                    </h4>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-3">
                      {generatedPlan.proTips.map((tip, idx) => (
                        <div
                          key={idx}
                          className="p-3 rounded-xl bg-white/10 backdrop-blur-sm border border-white/10 text-xs text-indigo-100 leading-relaxed"
                        >
                          💡 {tip}
                        </div>
                      ))}
                    </div>
                  </div>
                )}
              </div>
            )}
          </div>
        )}
      </main>
    </div>
  );
}

export default function StudyMaterialsPage() {
  return (
    <Suspense fallback={<div className="p-8 text-center text-gray-500">Loading Study Hub...</div>}>
      <StudyMaterialsContent />
    </Suspense>
  );
}
