<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@ taglib uri="/tags/primefaces-p" prefix="p"%>

<style type="text/css">
table.radio-table td { padding:10px; }
</style>
<f:view>

<h2> <ufrn:subSistema /> &gt; Formul�rio de Evolu��o da Crian�a </h2>
<p:resources />

	<div id="ajuda" class="descricaoOperacao" style="text-align: justify;">
		Essa tela tem por finalidade realizar a constru��o do formul�rio para a avalia��o dos discentes do n�vel Infantil e Fundamental, espec�fico para cada turma. 
		Dever� ser elaborado um formul�rio para cada bimestre definido pela institui��o.
		<br />
		<ul>
			<li> Legenda do cabe�alho do formul�rio:
				<ul>
					<li> <b> AVA: </b> Informa a forma de avalia��o da turma, podendo ser definidos da seguinte forma: </li>
					<li> <b> OBS: </b> Indica se o item do formul�rio permite o preenchimento de observa��es </li>
				</ul>
			</li>
		</ul>
		<ul>
			<li> Legenda da Forma de Avalia��o (AVA) ao adicionar um Conte�do ao formul�rio:			
				<ul>
					<c:forEach items="#{ formularioEvolucaoCriancaMBean.formasAvaliacao }" var="formasAva">
						<li>
							<b> <h:outputText id="formaAvaDescricaoaa" value="#{ formasAva.legenda }" /> </b> -
							<h:outputText id="formaAvaDescricao" value="#{ formasAva.opcoes }" />
						</li>
					</c:forEach>
				</ul>
			</li>
		</ul>
	</div>

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

	<div class="infoAltRem">
		<h:graphicImage url="/img/doc_copy.png" width="16px;" alt="Bloco" title="Bloco" rendered="#{ !formularioEvolucaoCriancaMBean.primeiroBimestre }"/>
		<h:commandLink value="Copiar Ficha do Bimestre Anterior" action="#{ formularioEvolucaoCriancaMBean.copiarBimestrerAnterior }" id="copiarBimestreAnterior"  
			onclick="return confirm('Deseja confirmar esta opera��o? Todos os dados inseridos no bimestre atual ser�o perdidos.');"
			rendered="#{ !formularioEvolucaoCriancaMBean.primeiroBimestre }">
		</h:commandLink>
	    <h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" style="overflow: visible;" />: Op��es
	    <h:graphicImage value="/img/check_cinza.png" style="overflow: visible;" />: Sem Observa��o
	    <h:graphicImage value="/img/check.png" style="overflow: visible;" />: Com Observa��o
	</div>

	<a4j:outputPanel id="table">

		<rich:dataTable id="repeater" value="#{ formularioEvolucaoCriancaMBean.itens }" var="itemForm" width="100%" 
			styleClass="listagem" rowKeyVar="row" style="border:0;" rowClasses="linhaPar,linhaImpar">
			
				 <f:facet name="header">
	                 <rich:columnGroup>
	                     <rich:column colspan="4" style="text-align: center; font-size: 18px;">
	                         <h:outputText value="#{ formularioEvolucaoCriancaMBean.obj.periodo }� Bimestre" />
	                     </rich:column>
	                 </rich:columnGroup>
                 </f:facet>
				
				<rich:column style="border:0;" width="40%;">
					<f:facet name="header"><h:outputText value="Descri��o"/></f:facet>
					<h:graphicImage url="/img/infantil/bloco.png" rendered="#{ itemForm.bloco }" width="16px;" alt="Bloco" title="Bloco" />
					<h:graphicImage url="/img/infantil/area.png" rendered="#{ itemForm.area }" width="21px;" alt="Area" title="Area" />
					<h:graphicImage url="/img/infantil/conteudo.png" rendered="#{ itemForm.conteudo }" width="26px;" alt="Conteudo" title="Conteudo" />
					<h:graphicImage url="/img/infantil/subcont.png" rendered="#{ itemForm.subCont }" width="31px;" alt="Sub-Conteudo" title="Sub-Conteudo"/>
					<h:graphicImage url="/img/infantil/objetivo.png" rendered="#{ itemForm.objetivo }" width="36px;" alt="Objetivo" title="Objetivo"/>
					<h:inputText id="txtItemDescricao" value="#{itemForm.item.descricao}" rendered="#{itemForm.editavel}" size="90" >
						<a4j:support reRender="txtItemDescricao" event="onchange" />
					</h:inputText>
					<h:outputText id="labelItemDescricao" value="#{itemForm.item.descricao}" rendered="#{!itemForm.editavel}"/>
				</rich:column>
				
				<rich:column style="border:0;text-align:center" width="4%;">
					<f:facet name="header"><h:outputText value="AVA"/></f:facet>
					<h:selectOneMenu value="#{ itemForm.item.formaAvaliacao.id }" id="turma" 
						disabled="#{ !itemForm.editavel }" rendered="#{ itemForm.conteudo }">
						<f:selectItems value="#{ tipoFormaAvaliacaoMBean.allCombo }"/>
						<a4j:support reRender="turma" event="onchange" />
					</h:selectOneMenu>
				</rich:column>
				
				<rich:column style="border:0;text-align:center" width="2%;">
					<f:facet name="header"><h:outputText value="OBS"/></f:facet>
					<h:selectBooleanCheckbox id="chkItemObservacao" value="#{itemForm.item.temObservacao}" 
						rendered="#{ itemForm.editavel && itemForm.conteudo }">
						<a4j:support reRender="chkItemObservacao" event="onchange" />
					</h:selectBooleanCheckbox>
					<h:graphicImage url="/img/check.png" alt="Com Observa��o" title="Com Observa��o" rendered="#{ !itemForm.editavel && itemForm.item.temObservacao && itemForm.conteudo }"/>
					<h:graphicImage url="/img/check_cinza.png" alt="Sem Observa��o" title="Sem Observa��o" rendered="#{ !itemForm.editavel && !itemForm.item.temObservacao && itemForm.conteudo }"/>
				</rich:column>

				<rich:column style="border:0;text-align:center" width="2%;">
					<h:graphicImage value="/img/biblioteca/emprestimos_ativos.png" title="Op��es" alt="Op��es">
						<rich:componentControl event="onclick" for="menuFormulario" operation="show">
				        	<f:param value="#{ row }" name="ordem"/>
				    	</rich:componentControl>
					</h:graphicImage>
				</rich:column>
				
		</rich:dataTable>

		<a4j:region rendered="#{ formularioEvolucaoCriancaMBean.vazia  }">
			<div style="text-align: center;">
				<h:graphicImage url="/img/adicionar.gif" width="16px;" alt="Bloco" title="Bloco"/>
				<h:commandLink value="Adicionar Bloco " action="#{ formularioEvolucaoCriancaMBean.adicionarItem }" 
					id="addBlocos">
					<f:param name="ordem" value="0"/>
					<f:param name="profundidade" value="0"/>
					<f:param name="periodo" value="#{ formularioEvolucaoCriancaMBean.obj.periodo }"/>
				</h:commandLink>
			</div>
		</a4j:region>
		
	</a4j:outputPanel>

	<table class="formulario" width="100%">
		<tfoot>
			<tr>
				<td colspan="4" style="text-align: center">
					<h:commandButton id="btnVoltar" action="#{ formularioEvolucaoCriancaMBean.anteriorBimestre }" value="<< Voltar" 
						rendered="#{ not formularioEvolucaoCriancaMBean.primeiroBimestre }"/>
					<h:commandButton id="btnCancelar" action="#{ formularioEvolucaoCriancaMBean.cancelar }" value="Cancelar" 
						onclick="#{confirm}" immediate="true" />
					<h:commandButton id="btnProx" action="#{ formularioEvolucaoCriancaMBean.proximoBimestre }" value="Gravar e Avan�ar >>" immediate="true" />
				</td>
			</tr>
		</tfoot>
	</table>

<%@include file="_menu_form.jsp"%>
	
</h:form>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>