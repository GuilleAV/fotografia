# Skill Registry

**Delegator use only.** Any agent that launches sub-agents reads this registry to resolve compact rules, then injects them directly into sub-agent prompts. Sub-agents do NOT read this registry or individual SKILL.md files.

See `_shared/skill-resolver.md` for the full resolution protocol.

## User Skills

| Trigger | Skill | Path |
|---------|-------|------|
| When working with Angular code, components, services, routing, or UI architecture | angular-architect | C:\Users\Usuario\.config\opencode\skills\angular-architect\SKILL.md |
| When creating a pull request, opening a PR, or preparing changes for review | branch-pr | C:\Users\Usuario\.config\opencode\skills\branch-pr\SKILL.md |
| When creating a GitHub issue, reporting a bug, or requesting a feature | issue-creation | C:\Users\Usuario\.config\opencode\skills\issue-creation\SKILL.md |
| When user asks to create a new skill, add agent instructions, or document patterns for AI | skill-creator | C:\Users\Usuario\.config\opencode\skills\skill-creator\SKILL.md |
| When writing Go tests, using teatest, or adding test coverage | go-testing | C:\Users\Usuario\.config\opencode\skills\go-testing\SKILL.md |
| When user says "judgment day", "judgment-day", "review adversarial", "dual review", "doble review", "juzgar", "que lo juzguen" | judgment-day | C:\Users\Usuario\.config\opencode\skills\judgment-day\SKILL.md |

## Compact Rules

Pre-digested rules per skill. Delegators copy matching blocks into sub-agent prompts as `## Project Standards (auto-resolved)`.

### angular-architect
- Standalone components only — no NgModules for new code
- Signals first: `signal()`, `computed()`, `effect()` for state; avoid BehaviorSubject for local state
- Always `OnPush` change detection; write zoneless-ready code
- Smart/Dumb pattern: containers handle state, presentational only receive inputs/emit outputs
- Use `input()`, `output()`, `model()` signal APIs — not `@Input()`/`@Output()`
- Use `@if`/`@for`/`@switch` control flow — NEVER `*ngIf`/`*ngFor`/`*ngSwitch`
- Use `@defer` for heavy components; `track` function in `@for`
- Use `inject()` for DI, not constructor injection
- Functional guards/resolvers/interceptors only — not class-based
- CSS custom properties for design tokens; component-scoped styles always
- No direct DOM manipulation — use `Renderer2` or template refs
- No `any` type — always proper TypeScript types
- No subscribing in components — use `toSignal()` or async pipe
- No `ngOnChanges` — use `computed()` or `effect()` with signal inputs

### branch-pr
- Every PR MUST link an approved issue (`Closes #N`) — no exceptions
- Every PR MUST have exactly one `type:*` label
- Branch naming: `type/description` (feat/, fix/, chore/, docs/, refactor/, etc.)
- Conventional commits required: `type(scope): description`
- PR body must contain: linked issue, type checkbox, summary, changes table, test plan
- Automated checks must pass before merge: issue reference, approved status, type label

### issue-creation
- Blank issues disabled — MUST use template (bug report or feature request)
- Every issue gets `status:needs-review` automatically on creation
- A maintainer MUST add `status:approved` before any PR can be opened
- Questions go to Discussions, not issues
- Search for duplicates before creating
- Bug report requires: description, steps to reproduce, expected/actual behavior, OS, agent, shell
- Feature request requires: problem description, proposed solution, affected area

### skill-creator
- Create skills when: repeated patterns, project-specific conventions, complex workflows, decision trees
- Don't create when: documentation exists, pattern is trivial, one-off task
- Structure: `skills/{name}/SKILL.md` + optional `assets/` (templates) + `references/` (local docs)
- Frontmatter required: name, description (with Trigger:), license (Apache-2.0), metadata.author, metadata.version
- DO: start with critical patterns, use tables for decisions, keep examples minimal, include Commands section
- DON'T: add Keywords section, duplicate docs, lengthy explanations, web URLs in references
- Register skill in AGENTS.md after creating

### go-testing
- Table-driven tests are the standard pattern for Go unit tests
- Bubbletea TUI: test `Model.Update()` directly for state changes
- Use `teatest.NewTestModel()` for interactive TUI flow testing
- Golden file testing for visual/rendering output verification
- Mock system dependencies via interfaces; use `t.TempDir()` for file operations
- Test both success and error cases; use `--short` flag to skip integration tests
- File organization: `*_test.go` alongside source, `testdata/` for golden files

### judgment-day
- Launch TWO judge sub-agents in parallel via delegate (async) — never sequential
- Neither judge knows about the other — no cross-contamination
- Orchestrator synthesizes: Confirmed (both agree), Suspect (one only), Contradiction (disagree)
- WARNING classification: `WARNING (real)` = normal user can trigger; `WARNING (theoretical)` = contrived scenario → report as INFO
- Round 1: present verdict, ask user to confirm fixes → Fix Agent → re-judge both judges
- Round 2+: only re-judge if confirmed CRITICALs; fix real WARNINGs inline without re-judge
- After 2 fix iterations, ASK user before continuing — never escalate automatically
- Orchestrator NEVER reviews code itself — only launches judges, reads results, synthesizes
- Resolve skills from registry BEFORE launching judges; inject Project Standards into all prompts

## Project Conventions

| File | Path | Notes |
|------|------|-------|
| .gitignore | C:\Fotografia\.gitignore | Backend: Java EE 8, Frontend: Angular, Docker, OS |
