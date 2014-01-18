<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>

	<h2> <ufrn:subSistema /> > Cadastro de Coordenador de Tutores do IMD > Buscar Pessoa</h2>

	<table class="formulario" style="width:75%;">
		<caption> Informe o critério de busca desejado</caption>
		<tbody>
			<tr>
				<td width="1px;"> <input type="radio" name="tipoBuscaPessoa"
						value="cpf"
						class="noborder" id="radioCpf" ${ coordenadorTutorIMD.tipoBuscaPessoa == "cpf" ? "checked='checked'" : "" } /> </td>
				<th width="5%;" style="text-align:left"> CPF:</th>
				<td>
					<h:inputText value="#{coordenadorTutorIMD.coordenador.pessoa.cpf_cnpj}" size="14" maxlength="14" onblur="formataCPF(this, event, null)" onkeypress="return formataCPF(this, event, null)" onfocus="getEl('radioCpf').dom.checked = true;">
						<f:converter converterId="convertCpf"/>
          				<f:param name="type" value="cpf" />						
					</h:inputText>
				</td>
			</tr>
			<tr>
				<td width="1px;" style="text-align:left"> <input type="radio" name="tipoBuscaPessoa" value="nome" class="noborder" id="radioNome" ${ coordenadorTutorIMD.tipoBuscaPessoa == "nome" ? "checked='checked'" : "" } /> </td>
				<th width="5%;"> Nome:</th>
				<td><h:inputText value="#{coordenadorTutorIMD.coordenador.pessoa.nome}" size="60" onfocus="getEl('radioNome').dom.checked = true;"/> </td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{coordenadorTutorIMD.buscarPessoa}" value="Buscar"/>
					<h:commandButton action="#{coordenadorTutorIMD.cancelarBuscaPessoa}" value="Cancelar"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

	<c:if test="${not empty coordenadorTutorIMD.resultadosBusca}">
		<br />
		<div class="infoAltRem">
    
			<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
			Selecionar Pessoa
				
		</div> 
		
		<table class="listagem"  style="width:75%;">
			<caption class="listagem">Selecione uma pessoa abaixo</caption>
			<thead>
				<tr>
					<td width="20%"><p align="center">CPF</p></td>
					<td>Nome</td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			<c:forEach items="#{coordenadorTutorIMD.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td><p align="center"><ufrn:format type="cpf_cnpj" valor="${item.cpf_cnpj }" /></p></td>
					<td>${item.nome}</td>
					<td width="3%">
					
						<h:commandLink action="#{coordenadorTutorIMD.escolhePessoa}"
							title="Selecionar Pessoa">
							<h:graphicImage url="/img/seta.gif"/>
							<f:param name="idPessoa" value="#{item.id}"/>
						</h:commandLink>
					</td>
				</tr>
			</c:forEach>
			</h:form>
		</table>
	</c:if>


</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>