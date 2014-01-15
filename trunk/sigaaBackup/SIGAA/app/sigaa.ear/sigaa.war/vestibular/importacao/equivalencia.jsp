<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>
<f:view>
	<p:resources/>
	<h2><ufrn:subSistema /> > Importar Aprovados de Vestibulares Externos</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Usu�rio,</b></p>
		<p>Foi criada uma lista de discentes com os dados importados. Por�m, alguns dados dever�o ser importados
		 utilizando-se uma tabela de equival�ncia como, por exemplo, Matriz Curricular.  
		 Informe qual campo do arquivo de dados indica o valor a ser utilizado na equival�ncia e,
		 em seguida, informe a equival�ncia de valores encontrados.</p>
		 <p><b>ATEN��O!</b> Alguns valores de equival�ncia s�o definidos automaticamente para facilitar o 
		 processo de migra��o, por�m <b>� importante que verifique os valores definidos</b>.</p>
		 <p>Informe as seguinte equival�ncias:</p>
		 <ul>
		 	<c:forEach items="#{ importaAprovadosOutrosVestibularesMBean.atributosComEquivalencia }" var="atributo">
		 		<li style="font-weight: ${importaAprovadosOutrosVestibularesMBean.atributoMapeavelEquivalencia.id == atributo.id ? 'bold' : ''};">${atributo.descricao}</li>
	 		</c:forEach>
		 </ul>
	</div>
	<br/>
	<h:form id="form">
		<c:if test="${ importaAprovadosOutrosVestibularesMBean.atributoMapeavelEquivalencia.usaSuggestionBox }">
			<div class="infoAltRem">
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />: Adicionar Equival�ncia 
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Equival�ncia
			</div>
		</c:if>
		<table class="formulario" width="100%" id="formAtributos">
		<caption>Informe a Equival�ncia para o Atributo ${ importaAprovadosOutrosVestibularesMBean.atributoMapeavelEquivalencia.descricao }</caption>
		<!-- Itens a mapeadar -->
		<c:if test="${ importaAprovadosOutrosVestibularesMBean.atributoMapeavelEquivalencia.usaSuggestionBox }">
			<c:if test="${ not empty importaAprovadosOutrosVestibularesMBean.valoresNaoMapeadosCombo}">
				<tr>
					<th class="required" width="25%">
						<h:selectOneMenu value="#{importaAprovadosOutrosVestibularesMBean.campo}" id="valorMapeado">
							<f:selectItems value="#{importaAprovadosOutrosVestibularesMBean.valoresNaoMapeadosCombo}" />
						</h:selectOneMenu>
					</th>
					<td>
						<h:inputText value="#{importaAprovadosOutrosVestibularesMBean.valor}" id="entidadeMapeada" style="width: 500px;"/>
						<rich:suggestionbox width="500" height="100" for="entidadeMapeada" id="sbEntidadeMapeada"
							minChars="2" nothingLabel="N�o Encontrado" 
							suggestionAction="#{importaAprovadosOutrosVestibularesMBean.autoComplete}" var="_entidadeEncapsulada"
							fetchValue="#{_entidadeEncapsulada.objeto}">
							<h:column>
								<h:outputText value="#{_entidadeEncapsulada.descritor}"/>
							</h:column>
						   <a4j:support event="onselect" 
						   		action="#{importaAprovadosOutrosVestibularesMBean.autoCompleteSelect}" >
								<f:setPropertyActionListener value="#{_entidadeEncapsulada.objeto}" 
									target="#{importaAprovadosOutrosVestibularesMBean.entidadeEquivalente}" />
						  </a4j:support>	
						</rich:suggestionbox>
					</td>
					<td>
						<h:commandButton action="#{ importaAprovadosOutrosVestibularesMBean.adicionarEquivalencia }" title="Adicionar Equival�ncia" id="adicionarEquivalencia"
						image="/img/adicionar.gif">
						</h:commandButton>
					</td>
				</tr>
			</c:if>
			<!-- Itens pr�-mapeados -->
			<tr>
				<td colspan="3" class="subFormulario">Valores Mapeados</td>
			</tr>
			<c:forEach items="#{ importaAprovadosOutrosVestibularesMBean.tabelaEquivalencia }" var="campoEquivalencia">
				<c:if test="${ campoEquivalencia.value.id > 0 }">
					<tr>
						<th class="rotulo" width="25%">
							<h:outputText value="#{ campoEquivalencia.key }:" />
						</th>
						<td>
							<h:outputText value="#{ campoEquivalencia.value }" />
						</td>
						<td width="2%">
							<h:commandLink action="#{ importaAprovadosOutrosVestibularesMBean.removerEquivalencia }" title="Remover Equival�ncia" id="removerEquivalencia">
								<h:graphicImage url="/img/delete.gif" alt="Remover Equival�ncia" />
								<f:param name="valor" value="#{ campoEquivalencia.key }"/>
							</h:commandLink>
						</td>
					</tr>
				</c:if>
			</c:forEach>
		</c:if>
		<c:if test="${ !importaAprovadosOutrosVestibularesMBean.atributoMapeavelEquivalencia.usaSuggestionBox }">
			<c:forEach items="#{ importaAprovadosOutrosVestibularesMBean.tabelaEquivalencia }" var="campoEquivalencia">
				<tr>
					<th class="required" width="25%">
						<h:outputText value="#{ campoEquivalencia.key }:" />
					</th>
					<td>
						<h:selectOneMenu value="#{ campoEquivalencia.value.id }" style="max-width:75%;" id="valorEquivalente">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{ importaAprovadosOutrosVestibularesMBean.valoresEquivalenciaCombo }"/>
						</h:selectOneMenu>
					</td>
					<td></td>
				</tr>
			</c:forEach>
		</c:if>
    	<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton value="<< Voltar" action="#{importaAprovadosOutrosVestibularesMBean.equivalenciaAnterior}"  id="equivalenciaAnterior" rendered="#{!importaAprovadosOutrosVestibularesMBean.primeiraEquivalencia}"/>
					<h:commandButton value="Recarregar o Arquivo" action="#{importaAprovadosOutrosVestibularesMBean.formUpload}"  id="voltar"/>
					<h:commandButton value="Cancelar" action="#{importaAprovadosOutrosVestibularesMBean.cancelar}" onclick="#{confirm}" id="cancelar"/>
					<h:commandButton value="Pr�ximo Passo >>" action="#{importaAprovadosOutrosVestibularesMBean.proximaEquivalencia}" id="submeterEquivalencias" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span> 
	</center>
	<br>
	<br>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>