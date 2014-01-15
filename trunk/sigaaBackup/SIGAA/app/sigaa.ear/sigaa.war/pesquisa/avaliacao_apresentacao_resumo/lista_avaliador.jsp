<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>
	<h:messages showDetail="true"></h:messages>
	<h2><ufrn:subSistema /> &gt; Lista de Apresentações de Resumos do CIC para Avaliação</h2>
	<h:outputText value="#{avaliacaoApresentacaoResumoBean.create}" />
	
	<c:if test="${not empty avaliacaoApresentacaoResumoBean.avaliacoes}">
	<div class="descricaoOperacao">
		
		Caro avaliador, o formulário de avaliação dos trabalhos estará disponível somente dentro do período do Congresso de Iniciação Científica 
		(<ufrn:format type="data" valor="${avaliacaoApresentacaoResumoBean.congresso.inicio}" /> a <ufrn:format type="data" valor="${avaliacaoApresentacaoResumoBean.congresso.fim}" />).
		
		<br/><br/>
		<strong>INSTRUÇÕES</strong>
		
		<ol>
			<li>
			Estando com as fichas de avaliação impressas entregues pela PROPESQ em mãos, 
			selecione um resumo da lista abaixo para visualizar o formulário de avaliação <em>on line</em>.
			</li>
			<li>
			Transcreva as informações solicitadas da ficha impressa correspondente para o formulário.
			</li>
			<li>
			Uma vez avaliado um trabalho, não é possível avaliá-lo novamente.
			</li>
			<li>
			Ao concluir as avaliações, clique no botão ao final da tela para encerrar.
			</li>
		</ol>
	</div>
	
	<br/>
	<center>
	<div class="infoAltRem">
		<h:graphicImage value="/img/view.gif" style="overflow: visible;" />: Visualizar Dados do Resumo
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: Avaliar Apresentação de Trabalho<br/>
	</div>
	</center>
	
	<h:form id="form">
		<t:dataTable value="#{avaliacaoApresentacaoResumoBean.avaliacoes}" var="av" id="avaliacoes" rowIndexVar="index" width="100%" rowClasses="linhaPar, linhaImpar" styleClass="listagem">
		
			<t:column>
				<f:facet name="header">
					<f:verbatim>Nº Painel</f:verbatim>
				</f:facet>
				<h:outputText value="#{av.resumo.numeroPainel}" />
			</t:column>
		
			<t:column>
				<f:facet name="header">
					<f:verbatim>Código</f:verbatim>
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
					<h:graphicImage url="/img/seta.gif" title="Avaliar Apresentação de Trabalho"/>
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
			<font color="red" style="font-weight: bold">Não há mais avaliações de apresentações de trabalhos destinadas a você.</font>
		</center>
	</c:if>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
