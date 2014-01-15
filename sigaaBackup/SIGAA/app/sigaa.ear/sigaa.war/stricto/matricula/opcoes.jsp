<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>
<link rel="stylesheet" type="text/css" href="${ctx}/css/matricula.css"/>
<link rel="stylesheet" type="text/css" href="${ctx}/css/stricto/matricula.css"/>

<jwr:script src="/javascript/jquery-ui/js/jquery-1.4.2.min.js" />
   
		<script>var J = jQuery.noConflict();</script>   
   
<jwr:style src="/css/jquery-ui/css/jquery-ui-1.8.4.custom.css" media="all"/>

<jwr:script src="/javascript/jquery-ui/js/jquery-ui-1.8.4.custom.min.js"/>
<jwr:script src="/javascript/jquery-layout.js"/>

<jwr:script src="/javascript/jquery-wtooltip.js"/>


<link rel="stylesheet" type="text/css" href="/sigaa/primefaces_resource/1.1/skins/sam/skin.css" /><link rel="stylesheet" type="text/css" href="/sigaa/primefaces_resource/1.1/jquery/plugins/ui/jquery.ui.dialog.css" />
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/core/core.js"></script>
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/dialog/dialog.js"></script>
<script type="text/javascript" src="/sigaa/primefaces_resource/1.1/primefaces/button/button.js"></script>

<f:view>
	<%@include file="/portais/discente/menu_discente.jsp" %>
	<h2> <ufrn:subSistema /> > Matr�cula Stricto Sensu > Tela Inicial </h2>

	<%-- Instru��es para o discente--%>
	<div class="descricaoOperacao">
		<p> Caro(a) Aluno(a), </p> <br />
		<p>
		 	Durante o per�odo de matr�cula o sistema permitir� que voc� efetue 
			sua solicita��o de matr�cula em componentes curriculares.
		</p>
		<p>
			Vale lembrar que a efetiva��o das solicita��es de matr�cula est�o sujeitas a aprova��o 
			do seu orientador ou da coordena��o do seu Programa de P�s-gradua��o. 
			Portanto, voc� s� estar� efetivamente matriculado ap�s a an�lise realizada por eles.
		</p>
		<p>
			<i>Para iniciar ou continuar uma solicita��o, clique em uma das op��es abaixo.</i>
		</p>
	</div>
	
	<%-- Op��es de matr�cula --%>
	<h:form>
	<div id="opcoes-matricula">
		<h3> Op��es de Matr�cula</h3>
		<c:set var="disciplinasClass" value="${matriculaStrictoBean.passivelMatriculaDisciplina ? '' : 'off'}" />
		<div class="item disciplinas">
			<h:commandLink action="#{matriculaStrictoBean.iniciarMatriculaDisciplinas}">
				Disciplinas
				<span> 
					${matriculaStrictoBean.discente.crTotaisIntegralizados} cr�ditos integralizados
				</span>
			</h:commandLink>
		</div>
		<div class="item proficiencia">
			<h:commandLink action="#{matriculaStrictoBean.iniciarMatriculaProficiencia}" id="linkIniciarMatriculaProficiencia">
				Exames de Profici�ncia
				<span> ${matriculaStrictoBean.statusProficiencias} </span>
			</h:commandLink>
		</div>		

		<c:set var="qualificacaoClass" value="${matriculaStrictoBean.passivelMatriculaQualificacao ? '' : 'off'}" />
		<div class="item qualificacao ${qualificacaoClass}">
			<h:commandLink action="#{matriculaStrictoBean.iniciarMatriculaQualificacao}" rendered="#{matriculaStrictoBean.passivelMatriculaQualificacao}" id="iniciarMatriculaQualificacao">
				Qualifica��o
				<span> ${matriculaStrictoBean.statusQualificacoes} </span>
			</h:commandLink>
			<c:if test="${!matriculaStrictoBean.passivelMatriculaQualificacao}">
				<a href="javascript:void(0);"> Qualifica��o <span>(matr�cula n�o permitida)</span> </a>
			</c:if>
		</div>

		<div class="item atividades">
			<h:commandLink action="#{matriculaStrictoBean.iniciarMatriculaComplementares}" id="linkIniciarMatriculaComplementares">
				Atividades Complementares
			</h:commandLink>
		</div>
		
		
		
		<c:set var="defesaClass" value="${matriculaStrictoBean.passivelMatriculaDefesa ? '' : 'off'}" />
		<div class="item defesa ${defesaClass}">
			<h:commandLink action="#{matriculaStrictoBean.iniciarMatriculaDefesa}" rendered="#{matriculaStrictoBean.passivelMatriculaDefesa}" id="linkIniciarMatricDefesa">
				Defesa
				<span> ${matriculaStrictoBean.statusDefesas} </span>
			</h:commandLink>
			<c:if test="${!matriculaStrictoBean.passivelMatriculaDefesa}">
				<a href="javascript:void(0);"> Defesa <span>(matr�cula n�o permitida)</span> </a>
			</c:if>
		</div>
		
		<div class="item outrosProgramas">
			<a href="#" onclick="dlg1.show(); return false;">
				Em outros programas
			</a>
		</div>		
		<div class="clear"> </div>

		<h3> Outras op��es </h3>
		<div class="item comprovante">
			<h:commandLink value="Comprovante de Matr�cula" action="#{ matriculaGraduacao.verComprovanteSolicitacoes}" id="linkComprovanteMatricula"/>
		</div>
		<div class="item historico">
			<h:commandLink value="Visualizar Hist�rico" action="#{historicoDiscente.historico}" id="linkParaVisualizarHistorico"/>
		</div>
	</div>
	
	</h:form>
	
	<p:dialog id="basicDialog" header="Programas" widgetVar="dlg1" modal="true" width="900" height="300">   
		<h:form id="formTurma">
			<c:if test="${not empty matriculaOutroProgramaStrictoBean.programasMatriculasAberta }">
				<h:outputText value="Selecione uma programa abaixo:" style="font-weight:bold;text-align:center;margin-top:10px;margin-bottom:10px;font-size:10pt;display:block;" styleClass="linkTurma"/>
				
				<div class="descricaoOperacao">
					Somente programas que est�o no per�odo de matr�cula est�o sendo exibidos nesta tela.
				</div>				
				
				<c:forEach items="#{matriculaOutroProgramaStrictoBean.programasMatriculasAberta}" var="p" varStatus="status">
				
					<h:commandLink action="#{matriculaOutroProgramaStrictoBean.selecionarPrograma}" styleClass="linkTurma">
						<f:param value="#{p.id}" name="idUnidade" />
						<span style="display:inline-block;width:97%;height:16px;overflow:hidden;vertical-align:middle;color:#003390;font-size:9pt;background:url('/sigaa/img/avancar.gif') ${status.index % 2 == 0 ? '#F0F0F0' : ''} no-repeat right;padding:5px;">
							<h:outputText value="#{p.nome}"/>
						</span>
					</h:commandLink>
				</c:forEach>
			</c:if>
			<c:if test="${empty matriculaOutroProgramaStrictoBean.programasMatriculasAberta }">
			<i>Nenhum programa no per�odo de matr�cula localizado.</i>
			</c:if>
		</h:form>  
	</p:dialog>  		
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>