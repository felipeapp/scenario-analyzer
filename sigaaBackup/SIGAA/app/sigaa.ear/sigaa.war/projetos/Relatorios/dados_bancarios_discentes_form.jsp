<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@include file="/WEB-INF/jsp/include/ajax_tags.jsp" %>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js"></script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />

<style>
        .center{
            text-align:center;
        } 
</style>

<f:view>
	<h2><ufrn:subSistema /> > Consultar Discentes de Projetos</h2>


<h:form id="form" prependId="false">

			<table class="formulario" width="90%">
			<caption>Busca por Discentes de Projetos</caption>
			<tbody>
				
				<tr>
					<td> <h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaTitulo}" id="selectBuscaTitulo" styleClass="noborder"/> </td>
			    	<td> <label for="titulo"> Título do Projeto: </label> </td>
			    	<td> <h:inputText id="buscaTitulo" value="#{discenteProjetoBean.buscaTitulo}" size="80" onfocus="javascript:$('form:selectBuscaTitulo').checked = true;"/></td>
			    </tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaAno}" id="selectBuscaAno" /></td>
			    	<td> <label for="ano"> Ano do Projeto: </label> </td>
			    	<td> <h:inputText label="Ano do Projeto" onkeyup="return formatarInteiro(this)" maxlength="4" value="#{discenteProjetoBean.buscaAno}" size="10" onfocus="javascript:$('form:selectBuscaAno').checked = true;"/></td>
			    </tr>
			    
			    <tr>
					<td><h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaAnoInicioBolsa}" id="selectBuscaAnoInicioFimBolsa" /></td>
			    	<td> <label for="anoAcao"> Assumiu Bolsa: </label> </td>
			    	<td>
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							size="10"
							value="#{discenteProjetoBean.dataInicioBolsa}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="inicioBolsa">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>	
						a
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimBolsa').checked = true;"
							size="10"
							value="#{discenteProjetoBean.dataFimBolsa}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="fimBolsa">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que o discente assumiu a bolsa no Projeto.</ufrn:help>						
					</td>
			    </tr>
			    
			    <tr>
					<td><h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaAnoInicioFinalizacao}" id="selectBuscaAnoInicioFimFinalizacao" /></td>
			    	<td> <label for="anoAcao"> Finaliza Bolsa: </label> </td>
			    	<td>
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							size="10"
							value="#{discenteProjetoBean.dataInicioFinalizacao}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="inicioFinalizacao">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>	
						a
						<t:inputCalendar
							onkeypress="return(formataData(this,event))"
							onchange="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							onfocus="javascript:$('form:selectBuscaAnoInicioFimFinalizacao').checked = true;"
							size="10"
							value="#{discenteProjetoBean.dataFimFinalizacao}"
							popupDateFormat="dd/MM/yyyy"
							renderAsPopup="true"
							renderPopupButtonAsImage="true"							
							maxlength="10"
							id="fimFinalizacao">
							<f:converter converterId="convertData"/>
						</t:inputCalendar>
						<ufrn:help img="/img/ajuda.gif">Período em que o discente é finalizado no Projeto.</ufrn:help>						
					</td>
			    </tr>

			    <tr>
					<td> <h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaEdital}" id="selectBuscaEdital" styleClass="noborder"/> </td>
			    	<td> <label for="edital"> Edital: </label> </td>
			    	<td>	    	
			    	 <h:selectOneMenu id="buscaEdital" value="#{discenteProjetoBean.buscaEdital}" onfocus="javascript:$('form:selectBuscaEdital').checked = true;">
						<f:selectItem itemLabel="-- SELECIONE --" itemValue="0"/>
			    	 	<f:selectItems value="#{editalMBean.allCombo}" />
			    	 </h:selectOneMenu>	    	 
			    	</td>
			    </tr>
				    
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaDiscente}"  id="selectBuscaDiscente" />
					</td>
			    	<td> <label> Discente: </label> </td>
					<td>
				
					 <h:inputText id="nomeDiscente" value="#{ discenteProjetoBean.discente.pessoa.nome }" size="60" onchange="javascript:$('form:selectBuscaDiscente').checked = true;"/>
					 <h:inputHidden id="idDiscente" value="#{ discenteProjetoBean.discente.id }"/>
				
					<ajax:autocomplete source="form:nomeDiscente" target="form:idDiscente"
						baseUrl="/sigaa/ajaxDiscente" className="autocomplete"
						indicator="indicatorDiscente" minimumCharacters="3" parameters="nivel=ufrn"
						parser="new ResponseXmlToHtmlListParser()" />
					<span id="indicatorDiscente" style="display:none; "> <img src="/sigaa/img/indicator.gif"  alt="Carregando..." title="Carregando..."/> </span>
					</td>
				</tr>
				
				<tr>
					<td><h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaCoordenador}" id="selectBuscaCoordenador" styleClass="noborder" />
					</td>
					<td><label>Coordenador(a):</label></td>
					<td>
					<h:inputHidden id="buscaServidor" value="#{discenteProjetoBean.coordenador.id}" />
					<h:inputText id="buscaNome"	value="#{discenteProjetoBean.coordenador.pessoa.nome}" size="70" onfocus="javascript:$('form:selectBuscaCoordenador').checked = true;" /> 
						<ajax:autocomplete
							source="form:buscaNome" target="form:buscaServidor"
							baseUrl="/sigaa/ajaxServidor" className="autocomplete"
							indicator="indicator" minimumCharacters="3" parameters="tipo=todos"
							parser="new ResponseXmlToHtmlListParser()" /> 
						<span id="indicator" style="display:none;"> <img src="/sigaa/img/indicator.gif" /> </span>
					</td>
				</tr>
		
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaVinculo}"  id="selectBuscaVinculo" />
					</td>
			    	<td> <label> Vínculo </label> </td>
			    	<td>
				    	 <h:selectOneMenu value="#{discenteProjetoBean.vinculoDiscente.id}" style="width: 200px"  onchange="javascript:$('form:selectBuscaVinculo').checked = true;">
				    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
							<f:selectItems value="#{tipoVinculoDiscenteBean.allAtivosAssociadosCombo}"/>
			 			 </h:selectOneMenu>
			    	 </td>
			    </tr>
		
		
				<tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteProjetoBean.checkBuscaCurso}"  id="selectBuscaCurso" />
					</td>
			    	<td> <label> Curso do Discente </label> </td>
			    	<td>
		
			    	 <h:selectOneMenu value="#{discenteProjetoBean.curso.id}" style="width: 400px"  onfocus="javascript:$('form:selectBuscaCurso').checked = true;">
			    	 	<f:selectItem itemValue="0" itemLabel="-- SELECIONE --"/>
						<f:selectItems value="#{curso.allCursoGraduacaoCombo}"/>
		 			 </h:selectOneMenu>
		
			    	 </td>
			    </tr>
			    
			    <tr>
					<td>
					<h:selectBooleanCheckbox value="#{discenteProjetoBean.checkGerarRelatorio}"  id="selectGerarRelatorio" />
					</td>
			    	<td> <label> <b>Gerar Relatório</b> </label> </td>
			    	<td></td>
			    </tr>
			    
			</tbody>
			<tfoot>
				<tr>
					<td colspan="3">
						<h:commandButton value="Buscar" action="#{ discenteProjetoBean.buscarDadosBancarios }"/>
						<h:commandButton value="Cancelar" action="#{ discenteProjetoBean.cancelar }" onclick="#{confirm}"/>
			    	</td>
			    </tr>
			</tfoot>
			</table>

</h:form>
<br/>

	
	<c:set var="discentes" value="#{discenteProjetoBean.listDiscentes}"/>
	<br/>
	

	<c:if test="${not empty discentes}">
		<div class="infoAltRem">
			 <h:graphicImage value="/img/projetos/user1_view.png" style="overflow: visible;"/>: Dados do Discente	
			 <h:graphicImage value="/img/report.png" style="overflow: visible;"/>: Plano de Trabalho	 
		</div>
		
		<h:form id="formLista">
		 <table class="listagem tablesorter" id="listagem">
		    <caption>Lista de Discentes Encontrados (${ fn:length(discentes) })</caption>
	
		      <thead>
		      	<tr>
		      		<th>Matrícula</th>
		        	<th>Discente</th>
		        	<th style="text-align: center;">Data de Cadastro</th>
		        	<th>Banco</th>
		        	<th>Agência</th>
		        	<th>Conta</th>
		        	<th>Operação</th>
		        	<th>Vínculo</th>
		        	<th>Situação</th>
		        	<th>&nbsp;</th>
		        	<th>&nbsp;</th>
		        </tr>
		 	</thead>
		 	<tbody>
		 	
			 	 <c:forEach items="#{discentes}" var="dp" varStatus="status">
		               <tr class="${ status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
		               	<td><h:outputText value="#{dp.discente.matricula}"/></td>
		               	<td><h:outputText value="#{dp.discente.nome}"/></td>
		               	<td align="center"><ufrn:format type="datahora" valor="${dp.planoTrabalhoProjeto.registroEntrada.data}" /></td>
		               	<td width="20%"><h:outputText value="#{dp.banco.codigo} - #{dp.banco.denominacao}" rendered="#{not empty dp.banco.denominacao }"/></td>
		               	<td><h:outputText value="#{dp.agencia}"/></td>
		               	<td><h:outputText value="#{dp.conta}"/></td>
		               	<td><h:outputText value="#{dp.operacao}"/></td>
		               	<td><h:outputText value="#{dp.tipoVinculo.descricao}"/></td>
		               	<td width="10%"><h:outputText value="#{dp.situacaoDiscenteProjeto.descricao}"/></td>
		               	<td>
		               	  	<h:commandLink title="Visualizar Discente" action="#{ discenteProjetoBean.view }" id="ver_discente">
							      <f:param name="idDiscenteProjeto" value="#{dp.id}"/>
							      <h:graphicImage url="/img/projetos/user1_view.png" />
							</h:commandLink>
		               	</td>
		               	<td  width="2%">
								<h:commandLink action="#{planoTrabalhoProjeto.view}" id="btnPlanoView"
									title="Visualizar Plano de Trabalho" style="border: 0;" rendered="#{not empty dp.planoTrabalhoProjeto}">
								      <f:param name="id" value="#{dp.id}"/>
						              <h:graphicImage url="/img/report.png" />
								</h:commandLink>
						</td>
		              </tr>
		          </c:forEach>
		          
		 	</tbody>
		 </table>
		<rich:jQuery selector="#listagem" query="tablesorter( {headers: {3: { sorter: false }, 4: { sorter: false }, 5: { sorter: false }, 6: { sorter: false },8: { sorter: false },9: { sorter: false } } });" timing="onload" />
	</h:form>
  </c:if>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>