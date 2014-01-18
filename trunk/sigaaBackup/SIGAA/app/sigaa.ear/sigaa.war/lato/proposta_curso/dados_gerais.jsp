<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.dominio.ModalidadeEducacao"%><f:view>

<h2><ufrn:subSistema /> &gt; Dados Básicos</h2>

<%@include file="include/_operacao.jsp"%>

<center>
	<div class="infoAltRem">
	    <h:graphicImage value="/img/adicionar.gif" style="overflow: visible;"/>: Adicionar 
	    <h:graphicImage value="/img/delete.gif" style="overflow: visible;"/>: Remover
	</div>
</center>

<h:form enctype="multipart/form-data" id="form">
  <table class=formulario width="100%" border="1">
   <caption class="listagem">Criação de Proposta de Curso Lato Sensu</caption>
	 <tr>
	  <td colspan="4">
	    <table class="subFormulario" width="100%">
		 <caption>Dados Básicos do Curso</caption>
		    <tr>
		    	<th class="obrigatorio">Tipo do Curso:</th>
				<td>
					<h:selectOneMenu  value="#{cursoLatoMBean.obj.tipoCursoLato.id}" id="tipoCurso" 
					valueChangeListener="#{cursoLatoMBean.changeTipoCurso}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoCursoLatoMBean.allCombo}" />
						<a4j:support event="onchange" reRender="form" />
					</h:selectOneMenu>
				</td>
		   </tr>
		   <c:if test="${cursoLatoMBean.obj.tipoCursoLato.id != 0}">
			   <tr>
					<th class="obrigatorio" style="width: 20%" colspan="1">${cursoLatoMBean.obj.nomeCurso}:</th>
					<td  width="60%" colspan="5"><h:inputText value="#{cursoLatoMBean.obj.nome}" maxlength="80" size="80" 
								id="nomeCurso" onkeyup="CAPS(this)" />
						<ufrn:help img="/img/ajuda.gif">Informe o nome do Curso.</ufrn:help>
					</td>
				</tr>
			</c:if>
			<tr>
		    	<th class="obrigatorio">Unidade Responsável:</th>
		    	<td colspan="4">
					<h:selectOneMenu value="#{cursoLatoMBean.obj.unidade.id}" id="unidade">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.allDeptosProgramasEscolasOrgaoCombo}" />
					</h:selectOneMenu>
					<ufrn:help img="/img/ajuda.gif">Departamento ou Programa de Pós-graduação que está oferecendo o curso.</ufrn:help>
				</td>
    		</tr>
			<tr>
		    	<th>Unidade Orçamentária:</th>
		    	<td colspan="4">
					<h:inputText value="#{cursoLatoMBean.obj.unidadeOrcamentaria.nome}" id="unidadeOrcamentaria" maxlength="250" style="width: 650px;"/>
					<rich:suggestionbox for="unidadeOrcamentaria" height="100" width="650"  minChars="3" id="suggestion"
						suggestionAction="#{unidadeAutoCompleteMBean.autocompleteNomeUnidade}" var="_unidade" 
					   	fetchValue="#{_unidade.codigoNome}">
						<h:column>
							<h:outputText value="#{_unidade.codigoNome}" />
						</h:column>
						<a4j:support event="onselect">
							<f:setPropertyActionListener value="#{_unidade.id}" target="#{cursoLatoMBean.obj.unidadeOrcamentaria.id}"  />
						</a4j:support>  
					</rich:suggestionbox>
				</td>
    		</tr>
 		    <tr>
		   		<th>Outras Unidades Envolvidas:</th>
		    	<td colspan="3">
					<h:selectOneMenu value="#{cursoLatoMBean.obj.unidadeCursoLato.unidade.id}" id="unidadeEnvolvida">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{unidade.allDeptosProgramasEscolasOrgaoCombo}" />
					</h:selectOneMenu>

				</td>
				<td>
					<h:commandLink action="#{cursoLatoMBean.cadastrarUnidadeEnvolvida}" >
						<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar"/>
						<f:param name="id" value="#{cursoLatoMBean.obj.unidadeCursoLato.unidade.id}"/>
					</h:commandLink>
				</td>
		    </tr>
		    <a4j:region rendered="#{ cursoLatoMBean.unidadesEnvolvidas }">
			   <tr>
			   	<th></th>
			   	<td colspan="2">
			   	   <table class="formulario" width="80%" align="left">
			   	   <caption class="listagem">Lista das Unidades Envolvidas Cadastradas</caption>
			   	    <c:forEach items="#{cursoLatoMBean.obj.unidadesCursoLato}" var="und" varStatus="status">
		             <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			   				 <td>${und.unidade.nome} - ${und.unidade.municipio.nome}</td>
			   	   		<td width="8%">
							<h:commandLink action="#{cursoLatoMBean.removerUnidadeEnvolvida}" onclick="#{confirmDelete}">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
								<f:param name="id" value="#{und.unidade.id}"/>
							</h:commandLink>
						</td>
			   	   	 </tr>
			   	    </c:forEach>
			   	   </table>
			   	   </td>
			   </tr>
			</a4j:region>
		   <tr>
		   		<th class="obrigatorio">Modalidade de Educação:</th>
			   	<td>
					<h:selectOneMenu  value="#{cursoLatoMBean.obj.modalidadeEducacao.id}" id="tipoModalidadeEducacao">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{modalidadeEducacao.allCombo}" />
						<a4j:support event="onchange" reRender="form" />
					</h:selectOneMenu>
			   	</td>
		   </tr>
   		   	 <c:set var="distancia" value="<%= (ModalidadeEducacao.A_DISTANCIA) %>" /> 
		     <c:if test="${cursoLatoMBean.obj.modalidadeEducacao.id == distancia}">
		   
			   <tr>
			   		<th class="obrigatorio">Pólo:</th>
			    	<td>
						<h:selectOneMenu value="#{cursoLatoMBean.obj.poloCurso.polo.id}" id="polo">
							<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
							<f:selectItems value="#{poloBean.allCombo}" />
						</h:selectOneMenu>
					
						<h:commandLink action="#{cursoLatoMBean.cadastrarPolos}" >
							<h:graphicImage value="/img/adicionar.gif" style="overflow: visible;" title="Adicionar"/>
							<f:param name="id" value="#{cursoLatoMBean.obj.poloCurso.polo.id}"/>
						</h:commandLink>
					</td>
			   </tr>
			 </c:if>
			 <c:if test="${not empty cursoLatoMBean.obj.polosCursos}"> 
			   <tr>
			   	<th></th>
			   	<td colspan="2">
			   	   <table class="formulario" width="80%" align="left">
			   	   <caption class="listagem">Lista dos Polos Cadastrados</caption>
			   	    <c:forEach items="#{cursoLatoMBean.obj.polosCursos}" var="polos" varStatus="status">
		             <tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
			   				 <td>${polos.polo.cidade}</td>
			   	   		<td width="8%">
							<h:commandLink action="#{cursoLatoMBean.removerPolo}" onclick="#{confirmDelete}">
								<h:graphicImage value="/img/delete.gif" style="overflow: visible;" title="Remover"/>
								<f:param name="id" value="#{polos.polo.id}"/>
							</h:commandLink>
						</td>
						 
			   	   	 </tr>
			   	    </c:forEach>
			   	   </table>
			   	   </td>
			   </tr>
			</c:if>
		   <tr>
    			<th class="obrigatorio">Carga Horária:</th>
				<td>
					<h:inputText value="#{cursoLatoMBean.obj.cargaHoraria}" size="4" maxlength="4" id="CargaHoraria" 
						 onkeyup="return formatarInteiro(this);" />
				</td>
    	   </tr>
  	       <tr>
		    	<th class="obrigatorio">Número de Vagas:</th>
				<td>
					<h:inputText value="#{cursoLatoMBean.obj.numeroVagas}" size="4" maxlength="4" id="numeroVagas" immediate="true"
						onkeyup="return formatarInteiro(this);" onchange="submit();" converter="#{ intConverter }" 
						valueChangeListener="#{ cursoLatoMBean.changeVagasServidores }" />
				</td>
		   </tr>
		   <tr>
				<th>Vagas Servidores Internos:</th>
				<td>
					<h:outputText value="#{cursoLatoMBean.obj.numeroVagasServidores}" id="numeroServidores" />
				</td>
		   </tr>
		   <tr>
				<th class="obrigatorio">Grande Área:</th>
				<td><h:selectOneMenu value="#{cursoLatoMBean.obj.grandeAreaConhecimentoCnpq.id}" 
							disabled="#{cursoLatoMBean.readOnly}" id="areaConhecimento" valueChangeListener="#{cursoLatoMBean.changeArea}"
							disabledClass="#{cursoLatoMBean.disableClass}" >
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{area.allCombo}" />
						<a4j:support event="onchange" reRender="area" />
					</h:selectOneMenu>
				</td>
		   </tr>
		   <tr>
				<th class="obrigatorio">Área: </th>
				<td><h:selectOneMenu value="#{cursoLatoMBean.obj.areaConhecimentoCnpq.id}" 
							id="area" disabled="#{cursoLatoMBean.readOnly}" 
							disabledClass="#{cursoLatoMBean.disableClass}" valueChangeListener="#{cursoLatoMBean.changeSubArea}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoLatoMBean.subArea}" />
						<a4j:support event="onchange" reRender="subarea" />
					</h:selectOneMenu>
				</td>
		   </tr>
		   <tr>
				<th>Subárea:</th>
				<td>
					<h:selectOneMenu value="#{cursoLatoMBean.obj.subAreaConhecimentoCnpq.id}" 
						id="subarea" disabled="#{cursoLatoMBean.disableClass}" 
						disabledClass="#{cursoLatoMBean.disableClass}" valueChangeListener="#{cursoLatoMBean.changeEspecialidade}">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoLatoMBean.especialidades}" />
						<a4j:support event="onchange" reRender="especialidade" />
					</h:selectOneMenu>
				</td>
		   </tr>
		   <tr>
				<th>Especialidade:</th>
				<td>
					<h:selectOneMenu  value="#{cursoLatoMBean.obj.especialidadeAreaConhecimentoCnpq.id}" 
						id="especialidade">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{cursoLatoMBean.propostaEspecialidade}" />
					</h:selectOneMenu>
				</td>
		   </tr>
		   <tr>
		    	<th class="obrigatorio">Tipo do Trabalho de Conclusão:</th>
		    	<td>
					<h:selectOneMenu value="#{cursoLatoMBean.obj.tipoTrabalhoConclusao.id}" id="tipoTrabalhoConclusao">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{tipoTrabalhoConclusao.combo}" />
					</h:selectOneMenu>
		    	</td>
		   </tr>
		   <tr>
		    	<th class="obrigatorio">Habilitação Específica:</th>
		    	<td>
					<h:inputText value="#{cursoLatoMBean.obj.habilitacaoEspecifica}" size="60" maxlength="120" id="habilitacaoEspecifica"/>
					<ufrn:help>Habilitação (título) dado ao discente concluído. Exemplos: Especialista em Energia Eólica, Especialista em Sistemas de Gestão Hospitalar.</ufrn:help>
		    	</td>
		   </tr>
		   <tr>
		    	<th>Banca Examinadora:</th>
		    	<td>
		    		<t:selectOneRadio id="bancaExaminadora" value="#{cursoLatoMBean.obj.bancaExaminadora}">  
                       <f:selectItem itemLabel="Sim" itemValue="true"/>  
                       <f:selectItem itemLabel="Não" itemValue="false" />  
                    </t:selectOneRadio>  
		    	</td>
		   </tr>
   		   <tr>
		    	<th>Financiamento:</th>
		    	<td>
					<h:selectOneMenu  value="#{cursoLatoMBean.obj.areaConhecimentoCnpq.especialidade.id}" 
						id="financiamento">
						<f:selectItem itemValue="0" itemLabel="-- SELECIONE --" />
						<f:selectItems value="#{financiamentoCursoLatoSensuMBean.allCombo}" />
					</h:selectOneMenu>
		    	</td>
		   </tr>
		   <tr>
		    	<th class="obrigatorio">Período Proposto do Curso:</th>
		    	<td colspan="3">
		    		<table>
		    		<tr>
		    			<th></th>
		    			<td>
					    	 <t:inputCalendar value="#{cursoLatoMBean.obj.dataInicio}" id="dataInicio" size="10" maxlength="10" 
		   						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
		   						renderAsPopup="true" renderPopupButtonAsImage="true" >
		     						<f:converter converterId="convertData"/>
							</t:inputCalendar> 
						</td>
						<th>&nbsp; a &nbsp;</th>
				    	<td>
					    	 <t:inputCalendar value="#{cursoLatoMBean.obj.dataFim}" id="dataFim" size="10" maxlength="10" 
	    						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
	    						renderAsPopup="true" renderPopupButtonAsImage="true" >
	      						<f:converter converterId="convertData"/>
							</t:inputCalendar> 
				    	</td>
		    		</tr>
		    		</table>
		    	</td>
		   </tr>
		   <tr>
		    	<th class="obrigatorio">Quantidade de Mensalidades:</th>
		    	<td colspan="3"><h:inputText value="#{cursoLatoMBean.obj.qtdMensalidades}" size="2" maxlength="2" id="qtdMensalidade" immediate="true"
						onkeyup="return formatarInteiro(this);" onchange="submit();" converter="#{ intConverter }" />
				</td>
			</tr>
			<tr>
		    	<th class="obrigatorio">Data de Vencimento da 1º Mensalidade:</th>
		    	<td colspan="3">
		    		<t:inputCalendar value="#{cursoLatoMBean.obj.dataPrimeiraMensalidade}" id="dataPrimeiraMensalidade" size="10" maxlength="10" 
   						onkeypress="return(formatarMascara(this,event,'##/##/####'))" popupDateFormat="dd/MM/yyyy" 
   						renderAsPopup="true" renderPopupButtonAsImage="true" >
     						<f:converter converterId="convertData"/>
					</t:inputCalendar> 
					<ufrn:help>A primeira mensalidade do curso terá esta data de vencimento. 
						As mensalidades subsequentes terão o mesmo dia de vencimento em cada mês.</ufrn:help>
				</td>
			</tr>
			<tr>
		    	<th class="obrigatorio">Valor da Mensalidade (R$):</th>
				<td colspan="3"><h:inputText id="valor"
					value="#{cursoLatoMBean.obj.valor}"
					size="8" maxlength="8"
					style="text-align: right" 
  					onfocus="javascript:select()" onkeydown="return formataValor(this, event, 2)" >
   						<f:converter converterId="convertMoeda"/>
				</h:inputText>
		   </tr>
		   <tr>
	 		    <th>Público Alvo:</th>
		   		<td colspan="5">
		   			<h:inputText value="#{cursoLatoMBean.obj.propostaCurso.publicoAlvo}" size="80" maxlength="150" 
		    			id="publicoAlvo" />
		   		</td>
		   </tr>
		   <tr>
				<th width="20%">Arquivo:</th>
				<td colspan="2">
					<t:inputFileUpload id="uFile" value="#{cursoLatoMBean.arquivoProposta}" storage="file" readonly="#{cursoLatoMBean.readOnly}" size="70"/>
				</td>
		   </tr>
	    </table>
   	   </td>
	  </tr>
	  <tr>
		<tfoot>
		   <tr>
				<td colspan="2">
					<h:commandButton value="Cancelar" action="#{cursoLatoMBean.cancelar}" onclick="#{confirm}" id="cancelar" />
					<h:commandButton value="Avançar >>" action="#{cursoLatoMBean.submeterDadosGerais}" id="cadastrar" />
				</td>
		   </tr>
		</tfoot>
   </table>
</h:form>

	<br />
	<center>
		<h:graphicImage url="/img/required.gif" style="vertical-align: top;" />
		<span class="fontePequena"> Campos de preenchimento obrigatório. </span>
	</center>
	<br />
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>