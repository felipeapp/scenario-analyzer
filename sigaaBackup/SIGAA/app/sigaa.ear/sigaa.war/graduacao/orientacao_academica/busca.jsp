<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<script type="text/javascript" src="/shared/loadScript?src=javascript/jquery.tablesorter.min.js">s</script>
<link rel="stylesheet" type="text/css" href="/shared/css/tablesorter/style.css" />


<style>
	table.listagem tr.curso td{
		background: #C8D5EC;
		font-weight: bold;
		padding-left: 20px;
	}
</style>

<f:view>
	<%@include file="/graduacao/menu_coordenador.jsp" %>
	<%@include file="/stricto/menu_coordenador.jsp" %>
	<h:form id="formulario">

	<h2>  <ufrn:subSistema /> > Cadastrar Orientação Acadêmica &gt; Buscar Discente</h2>

	<div class="descricaoOperacao">
		<p>Caro Usuário, <p>
		<p>
			Selecione os discentes para cadastrar a orientação acadêmica.
		</p>
		<p>	
			Será possível ordenar as colunas Nome e Matriz Curricular do resultado da busca, clicando nos títulos dessas colunas.
		</p>
	</div>

	<table width="100%" class="visualizacao">
		<tr>
			<th width="40%">Orientador Acadêmico:</th>
			<td><h:outputText value="#{orientacaoAcademica.orientador}"/></td>
		</tr>
		
		<tr>
			<th width="40%">Total de Orientandos:</th>
			<td><h:outputText value="#{orientacaoAcademica.totalOrientandos}"/></td>
		</tr>
		
		<c:if test="${not acesso.tecnico}">
			<tr>
				<th width="40%">Total de Orientandos de Mestrado:</th>
				<td><h:outputText value="#{orientacaoAcademica.equipe.maxOrientadosMestrado}"/></td>
			</tr>
	
			<tr>
				<th width="40%">Total de Bolsistas de Mestrado:</th>
				<td><h:outputText value="#{orientacaoAcademica.equipe.maxBolsistasMestrado}"/></td>
			</tr>
	
			<tr>
				<th width="40%">Total de Orientandos de Doutorado:</th>
				<td><h:outputText value="#{orientacaoAcademica.equipe.maxOrientadosDoutorado}"/></td>
			</tr>
	
			<tr>
				<th width="40%">Total de Bolsistas de Doutorado:</th>
				<td><h:outputText value="#{orientacaoAcademica.equipe.maxBolsistasDoutorado}"/></td>
			</tr>
		</c:if>

	</table>
	<br/>

	<table class="formulario" style="width:80%;">
		<caption> Informe o critério de busca desejado</caption>
		<tbody>
			<tr>
				<td width="2%">
					<h:selectBooleanCheckbox value="#{orientacaoAcademica.chkMatricula}"  id="chkMatricula" 
						styleClass="noborder" disabled="#{orientacaoAcademica.chkNome || orientacaoAcademica.chkAnoIngresso || orientacaoAcademica.chkSemOrientacao}" >
						<a4j:support event="onclick" reRender="formulario" />
					</h:selectBooleanCheckbox>
				</td>
				<th style="text-align: left;" width="20%"> Matrícula:</th>
				<td> 
					<h:inputText id="Matricula" value="#{orientacaoAcademica.discenteBusca.matricula}"
						disabled="#{orientacaoAcademica.chkNome || orientacaoAcademica.chkAnoIngresso || orientacaoAcademica.chkSemOrientacao}"
					 	onkeyup="return formatarInteiro(this);">
					 	<a4j:support event="onfocus" reRender="chkNome,nomeDiscente,chkMatricula,chkAnoIngresso,Ano,Periodo,chkSemOrientacao">
							<f:setPropertyActionListener value="#{true}" target="#{orientacaoAcademica.chkMatricula}"/>
						</a4j:support>	
					 </h:inputText>	
				</td>
			</tr>
			<tr>
				<td> <h:selectBooleanCheckbox value="#{orientacaoAcademica.chkNome}" disabled="#{orientacaoAcademica.chkMatricula}"  
						id="chkNome" styleClass="noborder" >
						<a4j:support event="onclick" reRender="Matricula,chkMatricula" />
					</h:selectBooleanCheckbox>	
				</td>
				<th style="text-align: left;" width="20%"> Nome do Discente:</th>
				<td> 
					<h:inputText value="#{orientacaoAcademica.discenteBusca.pessoa.nome}" 
						size="60" id="nomeDiscente" disabled="#{orientacaoAcademica.chkMatricula}">
						<a4j:support event="onfocus" reRender="chkNome,Matricula,chkMatricula">
							<f:setPropertyActionListener value="#{true}" target="#{orientacaoAcademica.chkNome}"/>
						</a4j:support>	
					</h:inputText>	 
				</td>
			</tr>
			<tr>
				<td> 
					<h:selectBooleanCheckbox value="#{orientacaoAcademica.chkAnoIngresso}" id="chkAnoIngresso" styleClass="noborder" 
						disabled="#{orientacaoAcademica.chkMatricula}">
						<a4j:support event="onclick" reRender="Matricula,chkMatricula" />
					</h:selectBooleanCheckbox> 
				</td>
				<th style="text-align: left;" width="20%"> Ano-Período de Ingresso:</th>
				<td>
					<h:inputText id="Ano" value="#{orientacaoAcademica.anoIngresso}" size="4" maxlength="4"  onkeyup="return formatarInteiro(this);"  disabled="#{orientacaoAcademica.chkMatricula}"> 
						<a4j:support event="onfocus" reRender="Matricula,chkMatricula,chkAnoIngresso">
							<f:setPropertyActionListener value="#{true}" target="#{orientacaoAcademica.chkAnoIngresso}"/>
						</a4j:support>	
					</h:inputText> 
					-
					<h:inputText id="Periodo" value="#{orientacaoAcademica.periodoIngresso}" size="1" maxlength="1"  onkeyup="return formatarInteiro(this);"  disabled="#{orientacaoAcademica.chkMatricula}">
						<a4j:support event="onfocus" reRender="Matricula,chkMatricula,chkAnoIngresso">
							<f:setPropertyActionListener value="#{true}" target="#{orientacaoAcademica.chkAnoIngresso}"/>
						</a4j:support>	
					</h:inputText>
				</td>
			</tr>
			<tr>
				<td> 
					<h:selectBooleanCheckbox value="#{orientacaoAcademica.chkSemOrientacao}" id="chkSemOrientacao" styleClass="noborder"  disabled="#{orientacaoAcademica.chkMatricula}">
						<a4j:support event="onclick" reRender="formulario" />
					</h:selectBooleanCheckbox>	 
				</td>
				<td colspan="2"><label for="checkTodos">Apenas os discentes sem orientação acadêmica</label></td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{orientacaoAcademica.buscarDiscente}" value="Buscar" id="buscaDiscentee"/>
					<h:commandButton action="#{orientacaoAcademica.cancelar}" value="Cancelar" onclick="#{confirm}" id="cancelarOrient"/>
				</td>
			</tr>
		</tfoot>
	</table>

	<c:if test="${not empty orientacaoAcademica.resultadoBusca}">
		<br/>
		<table class="listagem tablesorter" id="listagem">
			<caption> Discentes Encontrados (${fn:length(orientacaoAcademica.resultadoBusca)}) </caption>
			<thead>
				<tr>
					<th><a href="javascript:selectAllCheckBox()" style="fontColor: blue">Todos</a> </th>
					<th style="text-align: center;"> Matrícula </th>
					<th> Nome </th>
					<c:if test="${acesso.coordenadorCursoGrad}">
					<th> Matriz Curricular </th>
					</c:if>
					<th> Status </th>
					<c:if test="${acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao }">
					<th>Orientação</th>
					<th>Co-Orientação</th>
					</c:if>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{orientacaoAcademica.resultadoBusca}" var="discente" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td align="center">
						<input type="checkbox" name="selecionados" id="check_${discente.id}" value="${discente.id}" class="noborder"/>
					</td>
					<td align="center"> <label for="check_${discente.id}"> ${discente.matricula} </label> </td>
					<td> <input type="hidden" value="${discente.pessoa.nomeAscii}" id="nomeListagem"/> <label for="check_${discente.id}"> ${discente.nome} </label> </td>
					<c:if test="${acesso.coordenadorCursoGrad}">
					<td> ${discente.curriculo.matriz.descricaoMin} </td>
					</c:if>
					<td>${discente.statusString}</td>
					<c:if test="${acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao }">
					<td align="center">
						<input type="radio" name="tipo_${discente.id}" value="O" checked="checked">
					</td>
					<td align="center">
						<input type="radio" name="tipo_${discente.id}" value="C">
					</td>
					</c:if>
				</tr>
				</c:forEach>

			</tbody>
			<tfoot>
				<tr>
					<td>
					<c:if test="${acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao }">
						<td colspan="7" align="center">
					</c:if>
					<c:if test="${!(acesso.coordenadorCursoStricto || acesso.secretariaPosGraduacao)}">
						<td colspan="5" align="center">
					</c:if>
					<h:commandButton value="Adicionar Discentes" action="#{orientacaoAcademica.adicionarDiscentes}"/>
					<h:commandButton value="<< Voltar" action="#{orientacaoAcademica.telaOrientador}"/>
					<h:commandButton value="Cancelar" action="#{orientacaoAcademica.cancelar}" onclick="#{confirm}"/>
					</td>
				</tr>
			</tfoot>
		</table>
		<rich:jQuery selector="#listagem" query="tablesorter( 
					{headers: { 0: { sorter: false }, 1: { sorter: false } , 
								4: { sorter: false  }, 5: { sorter: false  }, 
								6: { sorter: false  } } } );" timing="onload" />
	</c:if>

	</h:form>
</f:view>
<script type="text/javascript">
<!--
var checkflag = "false";
function selectAllCheckBox() {
	    var div = document.getElementById('listagem');
	    var e = div.getElementsByTagName("input");
	   
	    var i;
	
	    if (checkflag == "false") {
	            for ( i = 0; i < e.length ; i++) {
	                    if (e[i].type == "checkbox"){ e[i].checked = true; }
	            }
	            checkflag = "true";
	    } else {
	            for ( i = 0; i < e.length ; i++) {
	                    if (e[i].type == "checkbox"){ e[i].checked = false; }
	            }
	            checkflag = "false";
	    }
	}
//-->
</script>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>