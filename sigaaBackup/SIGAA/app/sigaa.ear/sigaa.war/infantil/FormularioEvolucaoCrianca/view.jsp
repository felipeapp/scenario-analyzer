<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<style type="text/css">
table.radio-table td { padding:10px; }
</style>
<f:view>
<h2> <ufrn:subSistema /> &gt; Formulário de Evolução da Criança </h2>
 
<table class="visualizacao">
	<caption>Dados da Turma</caption>
	<tr>
		<th>Turma:</th>
		<td>
			<h:outputText escape="false" id="turma" value="#{formularioEvolucaoCriancaMBean.turma.descricaoTurmaInfantil}"/>
		</td>
	</tr>
	
	<tr>
		<th>Local:</th>
		<td>
			<h:outputText id="local" value="#{formularioEvolucaoCriancaMBean.turma.local}"/>
		</td>
	</tr>
	
	<tr>
		<th>Capacidade:</th>
		<td>
			<h:outputText id="capacidade" value="#{formularioEvolucaoCriancaMBean.turma.capacidadeAluno}"/>
			aluno(s)
		</td>
	</tr>

</table>
<br />

<h:form id="form">

	<a4j:outputPanel id="table">
			
			<rich:dataTable id="repeater" value="#{formularioEvolucaoCriancaMBean.itens}" var="itemForm" width="100%" 
				styleClass="listagem" rowKeyVar="row" style="border:1;">
			
				 <f:facet name="header">
	                 <rich:columnGroup>
	                     <rich:column colspan="4" style="text-align: center; font-size: 18px;">
	                         <h:outputText value="Resumo do Formulário da Evolução da Criança" />
	                     </rich:column>
	                 </rich:columnGroup>
                 </f:facet>
				
				<rich:subTable value="#{itemForm}" var="itemForm2" rendered="#{ itemForm.exibirBimestre }">
					<rich:column colspan="4" styleClass="subFormulario">
	                   <h:outputText value="#{ itemForm2.periodo }º Bimestre" />
	                </rich:column>
                </rich:subTable>

				<rich:subTable value="#{itemForm}" var="itemForm3" rendered="#{ itemForm.exibirBimestre }">
					<rich:column colspan="1" styleClass="subFormulario">
	                   <h:outputText value="Descrição" />
	                </rich:column>
					<rich:column colspan="3" styleClass="subFormulario" style="text-align:center;">
	                   <h:outputText value="Forma de Avaliação" />
	                </rich:column>
                </rich:subTable>

				<rich:column style="border:1;" width="40%;">
					<h:graphicImage url="/img/infantil/bloco.png" rendered="#{ itemForm.bloco }" width="16px;" alt="Bloco" title="Bloco" />
					<h:graphicImage url="/img/infantil/area.png" rendered="#{ itemForm.area }" width="21px;" alt="Area" title="Area" />
					<h:graphicImage url="/img/infantil/conteudo.png" rendered="#{ itemForm.conteudo }" width="26px;" alt="Conteudo" title="Conteudo" />
					<h:graphicImage url="/img/infantil/subcont.png" rendered="#{ itemForm.subCont }" width="31px;" alt="Sub-Conteudo" title="Sub-Conteudo"/>
					<h:graphicImage url="/img/infantil/objetivo.png" rendered="#{ itemForm.objetivo }" width="36px;" alt="Objetivo" title="Objetivo"/>
					<h:outputText id="labelItemDescricao" value="#{itemForm.item.descricao}" />
				</rich:column>

				<rich:column style="border:1; text-align: center;" width="2%;">
					<h:outputText id="labelItemAvaliacao" value="#{ itemForm.item.formaAvaliacao.headOpcoes[0] }" rendered="#{ not itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida }" />
					<h:selectBooleanCheckbox value="#{  }" id="item1" disabled="true" rendered="#{ itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida }" />
				</rich:column>

				<rich:column style="border:1; text-align: center;" width="2%;">
					<h:outputText id="labelItemAvaliacao1" value="#{ itemForm.item.formaAvaliacao.headOpcoes[1] }" rendered="#{ not itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida }"/>
					<h:selectBooleanCheckbox value="#{  }" id="item2" disabled="true" rendered="#{ itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida }"/>
				</rich:column>

				<rich:column style="border:1; text-align:center" width="2%;">
					<h:outputText id="labelItemAvaliacao2" value="#{ itemForm.item.formaAvaliacao.headOpcoes[2] }" rendered="#{ not itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida }" />
					<h:selectBooleanCheckbox value="#{  }" id="item3" disabled="true" rendered="#{ itemForm.objetivo && not itemForm.item.formaAvaliacao.indefinida }" />
				</rich:column>

				<rich:subTable rendered="#{ itemForm.exibirTextArea }" value="#{itemForm}" var="itemForm2">
					<f:facet name="header"><h:outputText value="OBS"/></f:facet>
					<rich:column style="border:1;" colspan="4">
						<h:inputTextarea disabled="true" rows="3" cols="134"></h:inputTextarea>
					</rich:column>				
                </rich:subTable>
				
			</rich:dataTable>
	
	</a4j:outputPanel>
	
	<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td colspan="4" style="text-align: center">
					<h:commandButton id="btnProx" action="#{ formularioEvolucaoCriancaMBean.cadastrar }" value="Cadastrar" />
					<h:commandButton id="btnVoltar" action="#{ formularioEvolucaoCriancaMBean.anteriorBimestre }" value="<< Voltar" />
					<h:commandButton id="btnCancelar" action="#{ formularioEvolucaoCriancaMBean.cancelar }" value="Cancelar" onclick="#{confirm}" immediate="true" />
				</td>
			</tr>
		</tfoot>
	</table>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>