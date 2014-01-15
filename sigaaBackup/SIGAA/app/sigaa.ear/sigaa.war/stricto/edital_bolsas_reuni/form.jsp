<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<script type="text/javascript" src="/shared/javascript/prototype-1.6.0.3.js" ></script>

<f:view>
	<a4j:keepAlive beanName="editalBolsasReuniBean" />
		
	<h2> <ufrn:subSistema /> &gt; Edital para Concessão de Bolsas REUNI de Assistência ao Ensino </h2>
	
	<div class="descricaoOperacao">
		<p> <b>	Caro usuário, </b> </p>
		<p>
			Abaixo poderão ser informados os principais campos de um edital de concessão de bolsas REUNI, necessários
			para a submissão de propostas.
		</p> 	
	</div>
	
	<h:form id="form" enctype="multipart/form-data">
	<table class="formulario" style="width: 100%;">
		<caption>Dados do Edital</caption>
		<tr>
			<th width="30%" class="required"> Descrição do Edital: </th>
			<td> 
				<h:inputText value="#{editalBolsasReuniBean.obj.descricao}" id="descricao" style="width: 95%;"/>
			</td>
		</tr>
		<tr>
			<th> Arquivo do Edital: </th>
			<td> 
				<t:inputFileUpload id="uFile" value="#{editalBolsasReuniBean.arquivoEdital}" storage="file"  size="70"/> 
			</td>
		</tr>
		<tr>
			<th class="required"> Período para Submissão de Propostas: </th>
			<td> 
				<t:inputCalendar value="#{editalBolsasReuniBean.obj.dataInicioSubmissao}" size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"
					id="dataInicioSubmissao" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"/>
				a
				<t:inputCalendar value="#{editalBolsasReuniBean.obj.dataFimSubmissao}" size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"
					id="dataFimSubmissao" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"/>
			</td>
		</tr>
		<tr>
			<th> Período para Indicação dos Bolsistas: </th>
			<td>
				<t:inputCalendar value="#{editalBolsasReuniBean.obj.dataInicioSelecao}" size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"
					id="dataInicioSelecao" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"/>
				a
				<t:inputCalendar value="#{editalBolsasReuniBean.obj.dataFimSelecao}" size="10" maxlength="10" popupDateFormat="dd/MM/yyyy"
					id="dataFimSelecao" renderAsPopup="true" renderPopupButtonAsImage="true"  onkeypress="return(formataData(this,event))"/>
			</td>
		</tr>
		
		<tr>
			<td colspan="2" class="subFormulario"> Componentes Curriculares com alta taxa de retenção </td>
		</tr>
		<a4j:region>
		<tr>
			<th>
				Componente Curricular:
			</th>
			<td>	
				<a4j:outputPanel id="inputComponentesPrioritarios">
				<h:inputText value="#{editalBolsasReuniBean.componenteCurricular.nome}" id="nomeComponente" style="width: 480px;"/>
				<rich:suggestionbox width="480" height="120" for="nomeComponente"  id="suggestion_componente"
					minChars="6" frequency="0" ignoreDupResponses="true" selfRendered="true" requestDelay="200" 
					suggestionAction="#{componenteCurricular.autocompleteGraduacao}" var="_componente" fetchValue="#{_componente.nome}">
					<h:column>
						<h:outputText value="#{_componente.codigo}"/>
					</h:column>
					<h:column>
						<h:outputText value="#{_componente.nome}"/>
					</h:column>
					<h:column>
						<h:outputText value="#{_componente.unidade.sigla}"/>
					</h:column>
					<a4j:support event="onselect" actionListener="#{editalBolsasReuniBean.adicionarComponente}"  id="adicionar_componente"
						reRender="inputComponentesPrioritarios, componentesPrioritarios" focus="nomeComponente" >
						<f:attribute name="componente" value="#{_componente}"/>
					</a4j:support>
				</rich:suggestionbox>
				
				<rich:spacer width="10"/>
	            <a4j:status>
	                <f:facet name="start">&nbsp;<h:graphicImage  value="/img/indicator.gif"/></f:facet>
	            </a4j:status>
				</a4j:outputPanel>
			</td>	
		</tr>
		<tr>
			<td colspan="2">
			<a4j:outputPanel id="componentesPrioritarios">
				<c:if test="${not empty editalBolsasReuniBean.obj.componentesPrioritarios}">
						<div class="infoAltRem">
					        <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover Componente
						</div>					
				</c:if>
			
			<t:dataTable value="#{editalBolsasReuniBean.obj.componentesPrioritarios}" var="_componente" style="width: 90%;"  id="datatable_componente"
				styleClass="listagem" rowClasses="linhaPar, linhaImpar" rendered="#{not empty editalBolsasReuniBean.obj.componentesPrioritarios}">

				<t:column>
					<f:facet name="header"><f:verbatim>Código</f:verbatim></f:facet>
					<h:outputText value="#{_componente.codigo}"/>
				</t:column>
				<t:column>
					<f:facet name="header"><f:verbatim>Nome</f:verbatim></f:facet>
					<h:outputText value="#{_componente.nome}"/>
				</t:column>
				<t:column>
					<f:facet name="header"><f:verbatim>Unidade</f:verbatim></f:facet>
					<h:outputText value="#{_componente.unidade.sigla}"/>
				</t:column>

				<t:column width="5%" styleClass="centerAlign">
					<a4j:commandButton image="/img/delete.gif" actionListener="#{editalBolsasReuniBean.removerComponente}" id="remover_componente"
						title="Remover Componente"	reRender="componentesPrioritarios">
							<f:attribute name="componente" value="#{_componente}"/>
					</a4j:commandButton>
				</t:column>					
			</t:dataTable>
			</a4j:outputPanel>
			</td>			
		</tr>		
		</a4j:region>
		
		<tfoot>
		<tr>
			<td colspan="2"> 
				<h:commandButton value="#{editalBolsasReuniBean.confirmButton}" action="#{editalBolsasReuniBean.cadastrar}" id="cadastrar"/> 
				<h:commandButton value="Cancelar" action="#{editalBolsasReuniBean.cancelar}" immediate="true" onclick="#{confirm}" id="editar"/>
			</td>
		</tr>
		</tfoot>
	</table>
	</h:form>	

	<br/>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> 
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
		<br><br>
	</center>
	
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
	