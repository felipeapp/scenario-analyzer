<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<a4j:keepAlive beanName="editalBolsasReuniBean" />
		
	<h2> <ufrn:subSistema /> &gt; Edital para Concessão de Bolsas REUNI de Assistência ao Ensino </h2>
	
	<h:form prependId="false">
	
	<input type="hidden" value="0" id="idArquivo" name="idArquivo"/>
	
	<div class="infoAltRem">
		<h:graphicImage value="/img/adicionar.gif"style="overflow: visible;"/> <h:commandLink action="#{editalBolsasReuniBean.preCadastrar}" value="Cadastrar novo edital"/>
		<h:graphicImage value="/img/view.gif" style="overflow: visible;"/>: Visualizar Arquivo do Edital
        <h:graphicImage value="/img/alterar.gif" style="overflow: visible;"/>: Alterar edital
        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover edital
	</div>
	
	<a4j:outputPanel id="editais">
	<t:dataTable value="#{editalBolsasReuniBean.all}" var="_edital"   id="datatable_edital"
		styleClass="listagem" rowClasses="linhaPar, linhaImpar">
		<f:facet name="caption"> <h:outputText value="Editais cadastrados"></h:outputText> </f:facet>

		<t:column>
			<f:facet name="header"><f:verbatim>Descrição</f:verbatim></f:facet>
			<h:outputText value="#{_edital.descricao}"/>
		</t:column>
		<t:column>
			<f:facet name="header"><h:outputText value="Período de Submissões" /></f:facet>
			<h:outputText value="#{_edital.dataInicioSubmissao}"> 
				<f:convertDateTime/>
			</h:outputText>
			a <h:outputText value="#{_edital.dataFimSubmissao}"> 
				<f:convertDateTime/>
			</h:outputText>
		</t:column>
		<t:column>
			<f:facet name="header"><h:outputText value="Período de Seleção de Bolsistas" /></f:facet>
			<h:outputText value="#{_edital.dataInicioSelecao}" rendered="#{not empty _edital.dataInicioSelecao}"> 
				<f:convertDateTime/>
			</h:outputText> 
			<h:outputText value=" a " rendered="#{not empty _edital.dataFimSelecao}" />
			<h:outputText value="#{_edital.dataFimSelecao}"> 
				<f:convertDateTime/>
			</h:outputText>
		</t:column>
		
		
		<t:column>
			<h:commandButton image="/img/view.gif" action="#{editalBolsasReuniBean.viewArquivo}" title="Visualizar Arquivo" 
				alt="Ver Arquivo"   onclick="$(idArquivo).value=#{_edital.idArquivoEdital};" id="verArquivo" />
		</t:column>
		
		<t:column>
			<h:commandLink title="Alterar edital" action="#{editalBolsasReuniBean.atualizar}" id="editar">
				<h:graphicImage url="/img/alterar.gif"/>
				<f:param name="id" value="#{_edital.id}"/>
			</h:commandLink>
		</t:column>
		<t:column>
			<h:commandLink title="Remover edital" action="#{editalBolsasReuniBean.remover}" onclick="#{confirmDelete}" id="remover">
				<h:graphicImage url="/img/delete.gif"/>
				<f:param name="id" value="#{_edital.id}"/>
			</h:commandLink>
		</t:column>					
	</t:dataTable>
	</a4j:outputPanel>	
		
	</h:form>	
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	