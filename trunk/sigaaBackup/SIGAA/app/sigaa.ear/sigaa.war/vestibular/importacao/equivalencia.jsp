<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@taglib uri="/tags/primefaces-p" prefix="p"%>
<script type="text/javascript">
	JAWR.loader.script('/javascript/prototype-1.6.0.3.js');
</script>
<f:view>
	<p:resources/>
	<h2><ufrn:subSistema /> > Importar Aprovados de Vestibulares Externos</h2>
	
	<div class="descricaoOperacao">
		<p><b>Caro Usuário,</b></p>
		<p>Foi criada uma lista de discentes com os dados importados. Porém, alguns dados deverão ser importados
		 utilizando-se uma tabela de equivalência como, por exemplo, Matriz Curricular.  
		 Informe qual campo do arquivo de dados indica o valor a ser utilizado na equivalência e,
		 em seguida, informe a equivalência de valores encontrados.</p>
		 <p><b>ATENÇÃO!</b> Alguns valores de equivalência são definidos automaticamente para facilitar o 
		 processo de migração, porém <b>é importante que verifique os valores definidos</b>.</p>
		 <p>Informe as seguinte equivalências:</p>
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
				<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" />: Adicionar Equivalência 
				<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Equivalência
			</div>
		</c:if>
		<table class="formulario" width="100%" id="formAtributos">
		<caption>Informe a Equivalência para o Atributo ${ importaAprovadosOutrosVestibularesMBean.atributoMapeavelEquivalencia.descricao }</caption>
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
							minChars="2" nothingLabel="Não Encontrado" 
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
						<h:commandButton action="#{ importaAprovadosOutrosVestibularesMBean.adicionarEquivalencia }" title="Adicionar Equivalência" id="adicionarEquivalencia"
						image="/img/adicionar.gif">
						</h:commandButton>
					</td>
				</tr>
			</c:if>
			<!-- Itens pré-mapeados -->
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
							<h:commandLink action="#{ importaAprovadosOutrosVestibularesMBean.removerEquivalencia }" title="Remover Equivalência" id="removerEquivalencia">
								<h:graphicImage url="/img/delete.gif" alt="Remover Equivalência" />
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
					<h:commandButton value="Próximo Passo >>" action="#{importaAprovadosOutrosVestibularesMBean.proximaEquivalencia}" id="submeterEquivalencias" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<br>
	<center>
		<html:img page="/img/required.gif" style="vertical-align: top;" /> <span class="fontePequena"> Campos de preenchimento obrigatório. </span> 
	</center>
	<br>
	<br>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>