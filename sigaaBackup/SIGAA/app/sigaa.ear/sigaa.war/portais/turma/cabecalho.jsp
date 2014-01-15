<%@page contentType="text/html; charset=ISO-8859-1" %>
<%@page import="br.ufrn.rh.dominio.Categoria"%>
<%@page import="br.ufrn.arq.caixa_postal.Mensagem"%>
<%@ taglib uri="/tags/jawr" prefix="jwr" %>
<%@ taglib uri="/tags/format" 	 prefix="fmt"  %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" 	 prefix="c"	   %>
<%@ taglib uri="/tags/functions" prefix="fn"   %>
<%@ taglib uri="/tags/sigaaFunctions" prefix="sf"  %>
<%@ taglib uri="http://java.sun.com/jsf/core" 	 prefix="f"    %>
<%@ taglib uri="http://java.sun.com/jsf/core" 	 prefix="h"    %>
<%@ taglib uri="http://myfaces.apache.org/tomahawk"  prefix="t"	   %>
<%@ taglib uri="/tags/ufrn" 	 prefix="ufrn" %>

<c:set var="ctx" value="${ pageContext.request.contextPath }"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">


<%@page import="br.ufrn.comum.dominio.Sistema"%><html>
    <head>
		<title>${ configSistema['siglaSigaa'] } - ${ configSistema['nomeSigaa'] }</title>
		<link rel="shortcut icon" href="${ctx}/img/sigaa.ico"/>
		
		<script type="text/javascript" src="/shared/jsBundles/jawr_loader.js" ></script>
		<script type="text/javascript">
			JAWR.loader.style('/javascript/ext-1.1/resources/css/ext-all.css', 'all');
			JAWR.loader.style('/bundles/css/sigaa_base.css','all');
			JAWR.loader.style('/css/ufrn_print.css', 'print');
	
			JAWR.loader.script('/bundles/js/sigaa_base.js');
			JAWR.loader.script('/bundles/js/ext1.js');
		
			JAWR.loader.style('/javascript/tablekit/style.css', 'all');
			JAWR.loader.script('/javascript/tablekit/fastinit.js');
			JAWR.loader.script('/javascript/tablekit/tablekit.js');
		</script>
		
		<link rel="stylesheet" type="text/css" media="all" href="/sigaa/css/turma-virtual/portal-turma.css"/>
		<link rel="stylesheet" type="text/css" media="all" href="/sigaa/css/turma-virtual/menu-turma-virtual.css"/>

		<script type="text/javascript">
			var mensagem;
			var initMensagem = function() {
				mensagem = new Mensagem();
			}
			YAHOO.util.Event.addListener(window,'load', initMensagem);
		</script>
	</head>

	<body>

	<div id="container">

		<div id="cabecalho">
			<div id="info-sistema">
				<h1> <span>${ configSistema['siglaInstituicao'] } - ${ configSistema['siglaSigaa'] } </span> - </h1>
				<h3> ${ configSistema['nomeSigaa'] } </h3>
				<c:if test="${not empty sessionScope.usuario}">
				<span class="sair-sistema"><a href="${ctx}/logar.do?dispatch=logOff"> SAIR </a></span>
				</c:if>
			</div>

			<div id="painel-usuario" <c:if test="${empty sessionScope.usuario}"> style="height: 20px;" </c:if>>
			<c:if test="${not empty sessionScope.usuario}">
			<div id="menu-usuario">
				<ul>
					<li class="modulos">
					<c:if test="${ not sessionScope.usuario.somenteConsultor }">
						<span id="modulos">
							<a href="#" id="show-modulos"> Módulos </a>
						</span>
					</c:if>
					</li>
					
					<li class="caixa-postal"><a	href="<%="/sigaa/abrirCaixaPostal.jsf?sistema="+String.valueOf(Sistema.SIGAA)%>" > 
					<c:if test="${ empty sessionScope.qtdMsgsNaoLidasCxPostal || sessionScope.qtdMsgsNaoLidasCxPostal <= 0 }">
					Caixa Postal					
					</c:if>
					<c:if test="${ not empty sessionScope.qtdMsgsNaoLidasCxPostal && sessionScope.qtdMsgsNaoLidasCxPostal > 0 }">
						<c:if test="${ sessionScope.qtdMsgsNaoLidasCxPostal > 999 }">
						Cx. Postal (999+)
						</c:if>
						<c:if test="${ sessionScope.qtdMsgsNaoLidasCxPostal <= 999 }">
						Cx. Postal (${ sessionScope.qtdMsgsNaoLidasCxPostal })
						</c:if>
					</c:if>
					</a></li>
					
					<li class="chamado">
						<a href="javascript://nop/"  accesskey="a" onclick="mensagem.show(<%=""+Mensagem.CHAMADO_SIGAA%>);"> Abrir Chamado </a>
					</li>
					<c:if test="${!acesso.administracao}">
						<li class="menus">
							<c:set var="categoriaDocente" value="<%= String.valueOf(Categoria.DOCENTE) %>"/>
							<c:if test="${sessionScope.usuario.servidor != null && sessionScope.usuario.servidor.categoria.id == categoriaDocente}">
								<a href="${ctx}/verPortalDocente.do">Menu Docente</a>
							</c:if>
							<c:if test="${sessionScope.usuario.discente != null && (sessionScope.usuario.servidor == null || (sessionScope.usuario.servidor.categoria.id != categoriaDocente))}">
								<a href="${ctx}/verPortalDiscente.do">Menu Discente</a>
							</c:if>
							<c:if test="${ sessionScope.usuario.somenteConsultor }">
								<a href="${ctx}/verPortalConsultor.do">Portal Consultor</a>
							</c:if>
						</li>
					</c:if>
					<c:if test="${acesso.administracao}">
						<li class="admin"><a href="${ctx}/verMenuAdministracao.do">Área Admin.</a> </li>
					</c:if>
					<li class="dados-pessoais"><a href="${ctx}/dados_pessoais/dados.jsf">Dados Pessoais</a> </li>
					<li class="ajuda"><a href="#" id="show-ajuda" rel="documento.getElementById('urlHelp')"> Ajuda </a></li>
				</ul>
			</div>
			<div id="info-usuario">
				<p class="usuario">
					<c:if test="${not empty sessionScope.usuario}">
			 			<ufrn:format type="texto" length="28" valor="${sessionScope.usuario.pessoa.nome }"/>
					</c:if>
					<c:if test="${sessionScope.usuarioAnterior != null}">
		 				<a href="${ctx}/administracao/usuario/logar_como.jsf">(Deslogar)</a>
		 			</c:if>
				</p>
				<p class="unidade">
					<ufrn:format type="texto" length="40" valor="${sessionScope.usuario.unidade.nome}"/>
					<c:if test="${not empty sessionScope.usuario.unidade}">
						(${sessionScope.usuario.unidade.codigoFormatado})
					</c:if>
					<c:if test="${ sessionScope.usuario.somenteConsultor }">
						Área de Conhecimento: <em>${ sessionScope.usuario.consultor.areaConhecimentoCnpq.nome }</em>
					</c:if>
				</p>
				<p class="periodo-atual">
					<c:if test="${calendarioAcademico != null}">
					Semestre atual: <strong>${calendarioAcademico.anoPeriodo}</strong>
					</c:if>
				</p>
			</div>
			</c:if>
		<div id="menu-principal"></div>

		</div>


	</div>
	<h:outputText value="#{ portalTurma.create }"/>
	<c:set var="permissao" value="${ portalTurma.permissao }"/>

	<div id="conteudo">
		<%@include file="/WEB-INF/jsp/include/erros.jsp"%>

		<div id="menu-cabecalho">
			<div id="trocarTurma">
				<h:form>
				<strong>Trocar turma:</strong>
				<select name="idTurma">
				<c:if test="${ !usuario.vinculoAtivo.vinculoDiscente }">
				<c:forEach items="${portalDocente.turmasAbertas}" var="turma" varStatus="status">
				<c:if test="${turma.disciplina.nivelDesc == 'Graduação' || turma.disciplina.nivelDesc == 'Técnico' || turma.disciplina.nivelDesc == 'Especialização' }">
					<option value="${turma.id}">${turma.descricaoSemDocente}</option>
				</c:if>
				</c:forEach>
				</c:if>
				<c:if test="${ usuario.vinculoAtivo.vinculoDiscente }">
				<c:forEach items="${portalDiscente.turmasAbertas}" var="turma" varStatus="status">
					<option value="${turma.id}">${turma.descricaoSemDocente}</option>
				</c:forEach>
				</c:if>
				</select>
				<h:commandButton action="#{portalTurma.entrarTurma}" value="Entrar"/>
				</h:form>
			</div>
			<h2>
				${portalTurma.turmaSelecionada.disciplina.codigo} - ${portalTurma.turmaSelecionada.disciplina.nome}
				<small> ${portalTurma.turmaSelecionada.ano}.${portalTurma.turmaSelecionada.periodo} - T${portalTurma.turmaSelecionada.codigo} </small>
			</h2>
		</div>

		<div id="menu-turma">
		<%@include file="menu_turma.jsp" %>
		</div>

		<c:if test="${param.expanded == null}">
		<div id="menu-direita">

			<h:form id="form-enquete">
			<h:outputText value="#{enquete.create}" />
			<c:set var="enqueteMaisAtual" value="${ enquete.enqueteMaisAtual }"/>
			<c:set var="respostaUsuarioEnquete" value="${enquete.respostaUsuarioEnquete}" />
			<c:set var="totalVotos" value="${enquete.totalVotos}" />
			<div id="enquetes-turma" class="item-menu-direita">
				<c:if test="${ empty enqueteMaisAtual }">
				 <h4>Enquetes</h4>
				 <center><br/>
				 <em>Nenhuma enquete cadastrada até o momento.</em>
				 </center>
				</c:if>
				<c:if test="${ not empty enqueteMaisAtual }">
				    <input type="hidden" value="${enqueteMaisAtual.id}" name="idEnquete" />
				    <h4>${enqueteMaisAtual.pergunta}</h4>
					<c:if test="${ respostaUsuarioEnquete == null }" >
				   	<ul class="enquete">
					   <c:forEach var="item" items="${ enqueteMaisAtual.respostas }">
					      <li><input type="radio" name="idEnqueteResposta" id="idEnqueteResposta" value="${item.id}" />  ${item.resposta}</li>
					   </c:forEach>
					</ul>
					<center><h:commandButton value="Votar" action="#{enqueteVotos.cadastrar}" id="votar-enquete"/></center>
				    </c:if>
				    <c:if test="${respostaUsuarioEnquete != null}" >
				    	<ul class="enquete">
				       <c:forEach var="item" items="${ enquete.estatisticaDeVotos }">
					      <li ${ item.id == respostaUsuarioEnquete.id ? 'class="votado"' : '' }>${item.resposta}<br/>
					      <span class="data"><fmt:formatNumber pattern="#" value="${((item.totalVotos*100)/totalVotos)}" />% (${item.totalVotos } Voto${item.totalVotos == 1 ? "" : "s" })</span>
					      </li>
					   </c:forEach>
					   </ul>
					</c:if>
				    <script type="text/javascript">document.getElementById('enquetes-turma').style.height ='auto';</script>
				</c:if>
			</div>
		 	</h:form>

		 	<div id="avaliacoes-turma" class="item-menu-direita">
				<h4>Avaliações</h4>
				<ul>
				<c:set var="avaliacoes" value="${ portalTurma.avaliacoes }"/>
					<c:if test="${ empty avaliacoes }">
						<center><br/>
						<em> Nenhum avaliação cadastrada </em>
						</center>
					</c:if>
					<c:if test="${ not empty avaliacoes }">
						<c:forEach items="${avaliacoes}" var="item">
							<li><span class="data"><ufrn:format name="item" property="data" type="data"/> ${item.hora}</span> <span class="descricao">${item.descricao}</span> </li>
						</c:forEach>
					</c:if>
				</ul>
			</div>

			<div id="foruns-turma" class="item-menu-direita">
				<h4>Fóruns</h4>

				<ul>
				<c:set var="foruns" value="${portalTurma.foruns}"/>
					<c:if test="${ not empty foruns }">
					<c:forEach items="${foruns}" var="forum">
					<li><a href="${ctx}/portais/turma/Forum/topicos.jsf?idForum=${forum.id}"> <span class="data"><ufrn:format name="forum" property="data" type="data"/></span> ${forum.titulo} </a></li>
					</c:forEach>
				</c:if>
				<c:if test="${ empty foruns}">
					<center>
					<br/>
					<em> Nenhum Fórum cadastrado </em>
					</center>
				</c:if>
				</ul>
			</div>

		</div>

		<div id="conteudo-turma" style="margin-right: 225px">
		</c:if>

		<c:if test="${param.expanded != null}">
		<div id="conteudo-turma">
		</c:if>