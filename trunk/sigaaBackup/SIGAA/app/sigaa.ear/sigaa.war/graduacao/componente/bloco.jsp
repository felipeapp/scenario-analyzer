<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<%@page import="br.ufrn.sigaa.ensino.dominio.TipoComponenteCurricular"%>
<script>
//Coloca um ponto entre o sétimo e oitavo caractere (DIM0123.4)  
function formataCodigo(v){
	v.value = v.value.toUpperCase();
	if (v.value.length > 7) {
		v.value = v.value.replace(/\./g,'');
    	v.value = v.value.substring(0,7) + '.' + v.value.substring(7); 
	}
	return v;  
}  
</script>
<f:view>
	<h2 class="title"><ufrn:subSistema/> &gt; Cadastrar Componente Curricular &gt; Subunidades de Blocos</h2>
	<h:form id="form">
		<table class="formulario" width="97%">
			<caption class="formulario">Cadastro SubUnidades</caption>
			<tr>
				<th class="required">Nome:</th>
				<td><h:inputText value="#{componenteCurricular.subUnidade.nome}" size="60" id="nomeSubUnidade" onkeyup="CAPS(this)" />
				</td>
			</tr>
			<tr>
				<th><h:selectBooleanCheckbox value="#{ componenteCurricular.defineCodigoSubUnidadeAutomaticamente }" 
					id="defineCodigoSubUnidadeAutomaticamente">
						<a4j:support reRender="form" event="onchange" />
					</h:selectBooleanCheckbox> 
				</th>
				<td>
					Definir automaticamente os códigos das subunidades deste bloco.
				</td>
			</tr>
			<c:if test="${ !componenteCurricular.defineCodigoSubUnidadeAutomaticamente }">
				<tr>
					<th class="required">Código:</th>
					<td><h:inputText value="#{componenteCurricular.subUnidade.codigo}" size="10" maxlength="10" 
						id="codigoSubUnidade" onkeyup="formataCodigo( this );"  onblur="formataCodigo( this );" />
					</td>
				</tr>
			</c:if>
			<tr>
				<th class="required">Tipo da Subunidade:</th>
				<td><h:selectOneMenu  value="#{componenteCurricular.subUnidade.tipoComponente.id}" id="tipo">
					<f:selectItem itemValue="0" itemLabel=" --- SELECIONE --- " />
					<f:selectItems value="#{componenteCurricular.tiposSubUnidades}" />
					<a4j:support reRender="form" event="onchange" />
				</h:selectOneMenu>
			</tr>
			
			<a4j:region rendered="#{ componenteCurricular.subUnidade.modulo || componenteCurricular.subUnidade.disciplina }">
				<tr>
					<th class="required" id="labelCHCreditoTeorica">
						<c:if test="${ componenteCurricular.subUnidade.modulo }">Carga Horária Teórica:</c:if>
						<c:if test="${ componenteCurricular.subUnidade.disciplina }">Créditos Teóricos:</c:if>
					</th>
					<td>
						<c:if test="${ componenteCurricular.subUnidade.modulo }">
							<h:inputText id="ch" value="#{componenteCurricular.subUnidade.detalhes.chAula}" size="3" maxlength="3" 
								converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
						</c:if>
						<c:if test="${ componenteCurricular.subUnidade.disciplina }">
							<h:inputText id="cr" value="#{componenteCurricular.subUnidade.detalhes.crAula}" size="3" maxlength="3" 
								converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<th class="required" id="labelCHCreditoPratica">
						<c:if test="${ componenteCurricular.subUnidade.modulo }">Carga Horária Prática:</c:if>
						<c:if test="${ componenteCurricular.subUnidade.disciplina }">Créditos Práticos:</c:if>
					</th>
					<td>
						<c:if test="${ componenteCurricular.subUnidade.modulo }">
							<h:inputText id="chPratica" value="#{componenteCurricular.subUnidade.detalhes.chLaboratorio}" size="3" maxlength="3" 
								converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
						</c:if>
						<c:if test="${ componenteCurricular.subUnidade.disciplina }">
							<h:inputText id="crPratico" value="#{componenteCurricular.subUnidade.detalhes.crLaboratorio}" size="3" maxlength="3" 
								converter="#{ intConverter }" onkeyup="return formatarInteiro(this);"/>
						</c:if>
					</td>
				</tr>
			</a4j:region>
			<tr>
				<th>Quantidade de Avaliações:</th>
				<td>
				<h:selectOneMenu id="numunidades"   value="#{componenteCurricular.subUnidade.numUnidades}">
					<f:selectItems value="#{componenteCurricular.numUnidadesPossiveis}" />
				</h:selectOneMenu> 
				</td>
			</tr>
			<tr>
				<th  class="required" valign="top">Ementa:</th>
				<td><h:inputTextarea value="#{componenteCurricular.subUnidade.ementa}" rows="4" cols="80"  id="subUnidade_ementa"/>
				 </td>
			</tr>
			<tr>
				<td colspan="2" align="center">
				<h:commandButton value="Adicionar Subunidade"
					rendered="#{!componenteCurricular.alterandoSubUnidade}"
					action="#{componenteCurricular.adicionarSubUnidade}" id="btAdicionarSubunidade"/>
				<h:commandButton value="Alterar Subunidade"
					rendered="#{componenteCurricular.alterandoSubUnidade}"
					action="#{componenteCurricular.alterarSubUnidade}" id="btAlterarSubunidade"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<c:if test="${!componenteCurricular.alterandoSubUnidade and (not empty componenteCurricular.obj.subUnidades)}">
						<div class="infoAltRem">
							<h:graphicImage value="/img/alterar.gif" style="overflow: visible;" />: Alterar Subunidade
							<h:graphicImage value="/img/delete.gif" style="overflow: visible;" />: Remover Subunidade
						</div>
						<table class="listagem" width="100%">
							<caption>Subunidades Cadastradas para esse Bloco</caption>
							<thead>
								<c:if test="${ not componenteCurricular.tecnico }">
									<th width="7%" style="text-align: right;">Cr</th>
								</c:if>
								<th width="7%" style="text-align: right;">Ch</th>
								<th width="10%">Tipo</th>
								<th>Código</th>
								<th>Nome</th>
								<th width="3%"></th>
								<th width="3%"></th>
							</thead>
							<c:set var="legenda" value="false" />
							<c:forEach items="#{componenteCurricular.obj.subUnidades}" var="subUnidade" varStatus="status">
							<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
								<c:if test="${ not subUnidade.tecnico }">
									<td style="text-align: right;">
										<c:if test="${subUnidade.disciplina}">${subUnidade.detalhes.crTotal} crs.</c:if>
									</td>
								</c:if>
								<td style="text-align: right;">
									${subUnidade.detalhes.chTotal}h
								</td>
								<td>${subUnidade.tipoComponente.descricao}</td>
								<td>
									<c:if test="${not empty subUnidade.codigo}">${subUnidade.codigo}</c:if>
									<c:if test="${empty subUnidade.codigo}">
										A DEFINIR<B><SUP>*</SUP></B>
										<c:set var="legenda" value="true" />
									</c:if>
								</td>
								<td>${subUnidade.nome}</td>
								<td>
									<h:commandLink id="alterar" title="Alterar Subunidade" action="#{componenteCurricular.verSubUnidade}" >
										<f:param name="nome" value="#{subUnidade.nome}" />
										<h:graphicImage url="/img/alterar.gif" style="overflow: visible;" alt="Alterar Subunidade" />
									</h:commandLink>
								</td>
								<td>
									<h:commandLink id="remover"  title="Remover Subunidade" action="#{componenteCurricular.removerSubUnidade}" onclick="#{confirmDelete}">
										<f:param name="nome" value="#{subUnidade.nome}"/>
										<h:graphicImage url="/img/delete.gif" style="overflow: visible;" alt="Remover Subunidade"/>
									</h:commandLink>
								</td>
							</tr>
							</c:forEach>
						</table>
						<c:if test="${legenda}">
							<b>*</b> o código da subunidade será definido automaticamente.
						</c:if>
				</c:if>
			</td>
			</tr>
		<tfoot>
			<tr>
				<td colspan="2">
					<h:commandButton value="<< Tipo do Componente Curricular" action="#{componenteCurricular.voltarDadosGerais}" id="tipoComponente"/>
					<h:commandButton value="<< Dados Gerais" action="#{componenteCurricular.submeterTipoComponente}" id="dadosGerais" />
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{componenteCurricular.cancelar}" id="cancelar" /> 
					<h:commandButton id="submissao" action="#{componenteCurricular.submeterBloco}" value="Avançar >>" />
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>
	<br/>
	<%@include file="/WEB-INF/jsp/include/mensagem_obrigatoriedade.jsp"%>
<script type="text/javascript">
<!--
function mudarTipo(sel) {
	var val = sel.options[sel.selectedIndex];
	if (val.value == '<%=TipoComponenteCurricular.DISCIPLINA%>') {
		$('labelCHCredito').innerHTML = 'Créditos';
	} else  if (val.value == '<%=TipoComponenteCurricular.MODULO%>') {
		$('labelCHCredito').innerHTML = 'Carga Horária';
	}

}
//-->
$('form:nomeSubUnidade').focus();
</script>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
