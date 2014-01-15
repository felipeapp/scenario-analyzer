<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style>
       .direita {text-align: right !important; }
        .esquerda {text-align: left !important; }
</style>
<f:view>
	<%@include file="/portais/docente/menu_docente.jsp"%>
	<h2><ufrn:subSistema /> &gt; Cadastro de Questionário de Avaliação</h2>
	<br />

	<table class="formulario" width="100%">
		<h:form id="form">
			<caption class="listagem">Cadastro de Questionário de Avaliação</caption>
			<h:inputHidden value="#{questionarioAvaliacao.confirmButton}" />
			<h:inputHidden value="#{questionarioAvaliacao.obj.id}" />
			<tr>
				<th class="obrigatorio">Título:</th>
				<td><h:inputText size="80" maxlength="300"
					readonly="#{questionarioAvaliacao.readOnly}"  value="#{questionarioAvaliacao.obj.descricao}" /></td>
			</tr>
			
			<tr>
				<td colspan="2" class="subFormulario">
					Itens de Avaliação
					<span class="required"></span>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<div class="infoAltRem" style="width: 100%">
						<h:graphicImage url="/img/add2.png" />: Adicionar item ao questionário
					    <img src="/shared/img/delete.gif" style="overflow: visible;"/>: Remover item do questionário
					</div>
				</td>
			</tr>
			<tr>
				<td colspan="2">
		            <rich:simpleTogglePanel switchType="client" label="Lista de Perguntas de Avaliação." bodyClass="body2" id="perguntasAvaliacao">
								<h:dataTable id="dtPerguntas" columnClasses="esquerda, esquerda, direita, direita, direita " binding="#{questionarioAvaliacao.itensUp}"
									value="#{questionarioAvaliacao.itensAux}" rowClasses="linhaPar, linhaImpar"  var="item"  width="100%" >
										<f:facet name="header">
											<rich:columnGroup columnClasses="esquerda, esquerda, direita, direita, direita " >
												<rich:column width="70%">
													 <f:facet name="header">Pergunta</f:facet>
												</rich:column>
												<rich:column width="12%">
													<f:facet name="header">Grupo</f:facet>
												</rich:column> 
												<rich:column style="text-align: right;" width="5%">
													<f:facet name="header">Peso</f:facet>
												</rich:column>
												<rich:column style="text-align: right;" width="8%" >
													<f:facet name="header">Nota Máxima</f:facet>
												</rich:column>
												<rich:column width="2%">
													<rich:spacer />
												</rich:column>
											</rich:columnGroup>
										</f:facet>
										
										<rich:column >
											<h:outputText value="#{item.pergunta.descricao}" />
										</rich:column>
										<rich:column >
											<h:selectOneMenu id="comboGrupo" value="#{item.grupo.id}">
												<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
		                            			<f:selectItems value="#{grupoAvaliacao.allComboGrupo}"/>
											</h:selectOneMenu>
										</rich:column>
										<rich:column >
										 	<h:inputText id="peso" value="#{item.peso}" label="Peso" size="4" maxlength="4" 
										 		onblur="comma2Point(this);" onkeydown="return(formataValor(this, event, 2));" />
										</rich:column>
										<rich:column>
											<h:inputText id="maxNota" value="#{item.notaMaxima}" label="Nota Máxima" size="4" maxlength="4"
												onblur="comma2Point(this);" onkeydown="return(formataValor(this, event, 2));" />
										</rich:column>
										<rich:column  >
											<h:commandButton action="#{questionarioAvaliacao.adicionarItemAvaliacao}"  	
												title="Adicionar item ao questionário" image="/img/add2.png" />
										</rich:column>
								</h:dataTable>				
					</rich:simpleTogglePanel>
					 <br/>
					 
					 <rich:separator height="2" lineType="dashed"/>
					 
					 <br/>
					<rich:simpleTogglePanel switchType="client" label="Lista de itens vinculados à avaliação " 
						bodyClass="body2" id="itensDeAvaliacao">
								<h:outputText value="<center><font color='red'><i> Nenhum item vinculado </i></font></center>" escape="false" rendered="#{questionarioAvaliacao.obj.itensAvaliacao == null}"/>
								<h:dataTable id="dtItensAvaliacao" columnClasses="esquerda, esquerda, direita, direita, direita"  rowClasses="linhaPar, linhaImpar" binding="#{questionarioAvaliacao.itensDown}" value="#{questionarioAvaliacao.obj.itensAvaliacao}" var="itemAval" width="100%">
										<f:facet name="header">
											<rich:columnGroup columnClasses="esquerda, esquerda, direita, direita, direita " rendered="#{not empty questionarioAvaliacao.obj.itensAvaliacao}">
												<rich:column width="70%">
													 <f:facet name="header">Pergunta</f:facet>
												</rich:column>
												<rich:column width="12%">
													<f:facet name="header">Grupo</f:facet>
												</rich:column> 
												<rich:column width="5%">
													<f:facet name="header">Peso</f:facet>
												</rich:column>
												<rich:column width="8%" >
													<f:facet name="header">Nota Máxima</f:facet>
												</rich:column>
												<rich:column width="2%">
													<rich:spacer />
												</rich:column>
											</rich:columnGroup>
										</f:facet>
										
										<rich:column >
											<h:outputText value="#{itemAval.pergunta.descricao}" />
										</rich:column>
										<rich:column >
											<h:outputText value="#{itemAval.grupo.descricao}" />
										</rich:column>
										<rich:column >
											<h:outputText value="#{itemAval.peso}" />
										</rich:column>
										<rich:column >
												<h:outputText value="#{itemAval.notaMaxima}" />
										</rich:column>		
										<rich:column >
											<h:commandButton  action="#{questionarioAvaliacao.removeItem}" onclick="#{confirm}"
												title="Remover item do questionário" image="/img/delete.gif"/>
										</rich:column>
								</h:dataTable>
					</rich:simpleTogglePanel>
			</td>
		</tr>
			
			<tfoot>
				<tr>
					<c:if test="${questionarioAvaliacao.confirmButton == 'Alterar' }" >
						<td colspan="2"><h:commandButton value="Alterar" action="#{questionarioAvaliacao.cadastrar}" />
					</c:if>
					<c:if test="${questionarioAvaliacao.confirmButton == 'Cadastrar'}">
						<td colspan="2"><h:commandButton value="Cadastrar" action="#{questionarioAvaliacao.cadastrar}" />
					</c:if>
					 <h:commandButton value="Cancelar" onclick="#{confirm}" action="#{questionarioAvaliacao.cancelarQuestionario}" /></td>
				</tr>
			</tfoot>
		</h:form>
	</table>
	<br>
	<div class="obrigatorio">Campos de preenchimento obrigatório.</div>
	
</f:view>
<script type="text/javascript">
	function comma2Point(texto){
		var t = parseFloat(texto.value.replace(',','.'));
		if(isNaN(t)){
			texto.value = '0.0';
		}else {
			texto.value = t;
		}
	}
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
