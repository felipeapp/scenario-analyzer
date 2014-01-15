<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsf/core" prefix="f" %>
<%@ taglib prefix="h" uri="http://java.sun.com/jsf/html" %>
<%@taglib uri="/tags/jawr" prefix="jwr"%>

<jwr:style src="/css/matricula.css" />
<jwr:script src="/javascript/graduacao/matricula.js" />

<script type="text/javascript">
<!--
	function markCheck(c) {
		var idMarcado = c.id.substring(0, c.id.indexOf('CHK'));
		var linha = $(idMarcado+'TR');
		var cor = (c.checked ? '#B5EEB5' : '');
		linha.style.backgroundColor=cor;
	}

	function marcarTurma(chk) {
		var cc = chk.id.substring(0, chk.id.indexOf('t_'));
		var checks = document.getElementsByName('selecaoTurmas');
		var c;
		for (i=0;i<checks.length;i++) {
			c = checks[i];
			if (c.checked && chk.id != c.id && c.id.indexOf(cc) >= 0) {
				c.checked = false;
				markCheck(c);
			}
		}
		markCheck(chk);
	}

	function marcarSemestre(chk) {
		var semestre = 's_' + chk.id;
		var checks = document.getElementsByName('selecaoTurmas');
		var compMarcado = ' ';
		for (i=0;i<checks.length;i++) {
			if (checks[i].id.indexOf(semestre) >= 0) {
				var id = checks[i].id.substring(checks[i].id.indexOf('cc_')+3,checks[i].id.indexOf('t_'));
				var proximoId;
				if ((i + 1) < checks.length){
					proximoId = checks[i+1].id.substring(checks[i+1].id.indexOf('cc_')+3,checks[i+1].id.indexOf('t_'));
				}
				if (id != compAtual) {
					if (compMarcado == '0') {
						checks[i].checked = chk.checked;
						markCheck(checks[i]);
					}
					compMarcado = '0';
				}
				if (proximoId != id && compMarcado == '0') {
					checks[i].checked = chk.checked;
					markCheck(checks[i]);
					compMarcado = id;
				}
				var compAtual = id;
				if (checks[i].id.indexOf('_temReserva') >= 0 && compAtual != compMarcado) {
					checks[i].checked = chk.checked;
					markCheck(checks[i]);
					compMarcado = compAtual;
				}
			}
		}
	}

	function esconderMostrar(comp, link) {
		var trs = $$('#lista-turmas-curriculo tr.turmas'+comp);

		for (i=0;i<trs.length;i++) {
			var linha = trs[i];
			var d = linha.style.display;
			if (d == 'none') {
				getEl(linha).show();
				link.innerHTML = '[ - ]';
			} else {
				getEl(linha).hide();
				link.innerHTML = '[ + ]';
			}
		}
	}

	function remarcarTurmasSubmetidas() {
		var turmas = '${turmasSubmetidas}';
		var turmasSubmetidas = turmas.split(',');
		var checks = document.getElementsByName('selecaoTurmas');
		for(j in turmasSubmetidas) {
			var t = turmasSubmetidas[j];
			for (i=0;i<checks.length;i++) {
				if (checks[i].value == t) {
					checks[i].checked = true;
					markCheck(checks[i]);
				}
			}
		}
	}

	function remarcarTurmasSubmetidas() {
		var turmas = '${turmasSubmetidas}';
		var turmasSubmetidas = turmas.split(',');
		var checks = document.getElementsByName('selecaoTurmas');
		for(j in turmasSubmetidas) {
			var t = turmasSubmetidas[j];
			for (i=0;i<checks.length;i++) {
				if (checks[i].value == t) {
					checks[i].checked = true;
					markCheck(checks[i]);
				}
			}
		}
	}
//
--></script>
<h:outputText value="#{matriculaGraduacao.create}" />

<f:subview id="menu">
	<c:if test="${matriculaGraduacao.solicitacaoMatricula and acesso.tutorEad}">
		<%@include file="/portais/tutor/menu_tutor.jsp" %>
	</c:if>
	<c:if test="${matriculaGraduacao.alunoRecemCadastrado and acesso.coordenadorCursoGrad}">
		<%@include file="/graduacao/menu_coordenador.jsp" %>
	</c:if>
		<%@include file="/stricto/menu_coordenador.jsp" %>
</f:subview>

<h2>
	<ufrn:subSistema /> &gt;
	<c:choose>
		<c:when test="${ matriculaGraduacao.solicitacaoMatricula }">
			Matrícula On-Line ${calendarioAcademico.anoPeriodo }
			<c:set value="true" var="hideSubsistema" />
		</c:when>
		<c:when test="${ matriculaGraduacao.compulsoria }">
			Matrícula Compulsória
		</c:when>
		<c:when test="${ matriculaGraduacao.foraPrazo }">
			Matrícula Fora do Prazo
		</c:when>
		<c:otherwise>
			Matrícula de Discente
		</c:otherwise>
	</c:choose> 
	&gt; ${ matriculaGraduacao.operacaoAtual }
</h2>

