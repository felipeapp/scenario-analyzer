<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Lista de Apresenta��es de Resumos do CIC para Avalia��o</h2>
	<h:outputText value="#{avaliacaoApresentacaoResumoBean.create}" />
	
	<c:if test="${not empty avaliacaoApresentacaoResumoBean.avaliacoes}">
	<div class="descricaoOperacao">
		
		Caro avaliador, o formul�rio de avalia��o dos trabalhos estar� dispon�vel somente dentro do per�odo do Congresso de Inicia��o Cient�fica 
		(<ufrn:format type="data" valor="${avaliacaoApresentacaoResumoBean.congresso.inicio}" /> a <ufrn:format type="data" valor="${avaliacaoApresentacaoResumoBean.congresso.fim}" />).
		
		<br/><br/>
		<strong>INSTRU��ES</strong>
		
		<ol>
			<li>
			Estando com as fichas de avalia��o impressas entregues pela PROPESQ em m�os, 
			selecione um resumo da lista abaixo para visualizar o formul�rio de avalia��o <em>on line</em>.
			</li>
			<li>
			Transcreva as informa��es solicitadas da ficha impressa correspondente para o formul�rio.
			</li>
			<li>
			Uma vez avaliado um trabalho, n�o � poss�vel avali�-lo novamente.
			</li>
			<li>
			Ao concluir as avalia��es, clique no bot�o ao final da tela para encerrar.
			</li>
		</ol>
	</div>
	
	<br/>
	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Dados do Resumo
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Avaliar Apresenta��o de Trabalho<br/>
	</div>
	</center>
	
	<h:form id="form">
		<t:dataTable value="#{avaliacaoApresentacaoResumoBean.avaliacoes}" var="av" id="avaliacoes" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
		
			<t:column>
				<f:facet name="header">
					<f:verbatim>N� Painel</f:verbatim>
				</f:facet>
				<h:outputText value="#{av.resumo.numeroPainel}" />
			</t:column>
		
			<t:column>
				<f:facet name="header">
					<f:verbatim>C�digo</f:verbatim>
				</f:facet>
				<h:outputText value="#{av.resumo.codigo}" />
			</t:column>
			
			<t:column>
				<f:facet name="header">
					<f:verbatim>Autor</f:verbatim>
				</f:facet>
				<h:outputText value="#{av.resumo.autor.nome}" />
			</t:column>
			
			<t:column>
				<f:facet name="header">
					<f:verbatim>Orientador</f:verbatim>
				</f:facet>
				<h:outputText value="#{av.resumo.orientador.nome}" />
			</t:column>
			
			<t:column width="5%" styleClass="centerAlign">
				<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
				<h:commandLink actionListener="#{avaliacaoApresentacaoResumoBean.redirecionar}">
					<f:param name="id" value="#{av.resumo.id}"/>
					<f:param name="url" value="/pesquisa/resumoCongresso.do?dispatch=view&id="/>
					<h:graphicImage url="/img/view.gif" title="Visualizar Dados do Resumo"/>
				</h:commandLink>
			</t:column>
			
			<t:column width="5%" styleClass="centerAlign">
				<f:facet name="header"><f:verbatim></f:verbatim></f:facet>
				<h:commandLink action="#{avaliacaoApresentacaoResumoBean.popularAvaliacao}" rendered="#{not av.avaliado and avaliacaoApresentacaoResumoBean.periodoCongresso}">
					<f:param name="id" value="#{av.id}"/>
					<h:graphicImage url="/img/seta.gif" title="Avaliar Apresenta��o de Trabalho"/>
				</h:commandLink>
			</t:column>
		</t:dataTable>
		
		<center>
			<br />
			<h:commandButton id="btnConcluir" value="Cancelar" action="#{avaliacaoApresentacaoResumoBean.cancelar}" />
		</center>
		
	</h:form>
	</c:if>
	
	<c:if test="${empty avaliacaoApresentacaoResumoBean.avaliacoes}">
		<center>
			<br />
			<font color="red" style="font-weight: bold">N�o h� mais avalia��es de apresenta��es de trabalhos destinadas a voc�.</font>
		</center>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
