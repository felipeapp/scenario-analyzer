<%@taglib prefix="h" uri="http://java.sun.com/jsf/html"%>
<%@taglib prefix="f" uri="http://java.sun.com/jsf/core"%>
<%@taglib prefix="a4j" uri="/tags/a4j"%>
<%@taglib prefix="rich" uri="/tags/rich"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>

<style>
	tr.view td {text-align: left; width: 20%}
	tr.validada td {text-align: center; color: green;}
	tr.view th {text-align: right; font-weight: bold; width: 10%}
</style>

<a4j:keepAlive beanName="carregarDadosProducoesMBean"/>
<rich:modalPanel id="view" minHeight="500" minWidth="700" height="100" width="300">  
        <f:facet name="header">  
            <h:panelGroup>
                <h:outputText value="Produção Intelectual"></h:outputText>
            </h:panelGroup>
        </f:facet>
        <f:facet name="controls">
            <h:panelGroup>
	            <h:outputLink value="#" id="btn1">  
	   		         <h:graphicImage value="/img/close.png"  style="margin-left:5px; cursor:pointer; border: none" />  
	                 <rich:componentControl for="view" attachTo="btn1" operation="hide" event="onclick" />  
	            </h:outputLink>
            </h:panelGroup>
        </f:facet>
       <table class="formulario" width="100%">
       	<caption>${carregarDadosProducoesMBean.obj.tipoProducao.descricao}</caption>
       	<c:if test="${carregarDadosProducoesMBean.artigo}">
			<%@include file="/prodocente/include_painel/artigo.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.captuloLivro}">
			<%@include file="/prodocente/include_painel/capitulo_livro.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.livro}">
			<%@include file="/prodocente/include_painel/livro.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.evento}">
			<%@include file="/prodocente/include_painel/evento.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.textoDidatico}">
			<%@include file="/prodocente/include_painel/texto_didatico.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.audioVisual}">
			<%@include file="/prodocente/include_painel/audio_visual.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.exposicaoApresentacao}">
			<%@include file="/prodocente/include_painel/exposicao_apresentacao.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.montagem}">
			<%@include file="/prodocente/include_painel/exposicao_apresentacao.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.programacaoVisual}">
			<%@include file="/prodocente/include_painel/programacao_visual.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.maquetePrototipo}">
			<%@include file="/prodocente/include_painel/maquete_prototipo.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.patente}">
			<%@include file="/prodocente/include_painel/patente.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.trabalhoConclusao}">
			<%@include file="/prodocente/include_painel/conclusao_curso.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.comissaoJulgadora}">
			<%@include file="/prodocente/include_painel/conclusao_curso.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.premio}">
			<%@include file="/prodocente/include_painel/premio.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.bolsaObtida}">
			<%@include file="/prodocente/include_painel/bolsa_obtida.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.visitaCientifica}">
			<%@include file="/prodocente/include_painel/visita_cientifica.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.participacaoEvento}">
			<%@include file="/prodocente/include_painel/participacao_evento.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.participacaoSociedade}">
			<%@include file="/prodocente/include_painel/participacao_sociedade.jsp"%>
		</c:if>
       	<c:if test="${carregarDadosProducoesMBean.participacaoColegiado}">
			<%@include file="/prodocente/include_painel/participacao_colegiado.jsp"%>
		</c:if>
	
		<c:if test="${!carregarDadosProducoesMBean.trabalhoConclusao 
						&& !carregarDadosProducoesMBean.comissaoJulgadora
						&& !carregarDadosProducoesMBean.premio
						&& !carregarDadosProducoesMBean.bolsaObtida
						&& !carregarDadosProducoesMBean.visitaCientifica
						&& !carregarDadosProducoesMBean.participacaoEvento
						&& !carregarDadosProducoesMBean.participacaoSociedade
						&& !carregarDadosProducoesMBean.participacaoColegiado}">
       	<tr>
       		<td colspan="8">
       			<table class="subFormulario" width="100%">
       				<caption>Quantitativos</caption>
       					<tr class="view">
							<th>Docentes (incluindo você):</th>
							<td>${carregarDadosProducoesMBean.obj.numeroDocentes}</td>
							<th>Doc. de outros Departamentos:</th>
							<td>${carregarDadosProducoesMBean.obj.numeroDocentesOutros}</td>
							
							<th></th>
							<td></td>
       					</tr>
       					<tr class="view">
							<th>Estudantes:</th>
							<td>${carregarDadosProducoesMBean.obj.numeroEstudantes}</td>
							
							<th>Técnicos:</th>
							<td>${carregarDadosProducoesMBean.obj.numeroTecnicos}</td>
							
							<th>Outros:</th>
							<td>${carregarDadosProducoesMBean.obj.numeroOutros}</td>
       					</tr>
       			</table>
       		</td>
       	</tr>
       	</c:if>
       </table>
</rich:modalPanel>  