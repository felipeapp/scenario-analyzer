<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h:form>

	<h:inputHidden value="#{buscaPessoa.codigoOperacao}"/>

	<h2> <ufrn:subSistema /> > ${buscaPessoa.operacao.nome} > Buscar Pessoa</h2>

	
	<div class="infoAltRem">
    
		<h:graphicImage value="/img/seta.gif" style="overflow: visible;" />: 
		Selecionar Pessoa
			
	</div> 

	<table class="formulario" style="width:75%;">
		<caption> Informe o critério de busca desejado</caption>
		<tbody>
			<tr>
				<td width="1px;"> <input type="radio" name="tipoBusca"
						value="cpf"
						class="noborder" id="radioCpf" ${ buscaPessoa.tipoBusca == "cpf" ? "checked='checked'" : "" } /> </td>
				<th width="5%;" style="text-align:left"> CPF:</th>
				<td>
					<h:inputText value="#{buscaPessoa.obj.cpf_cnpj}" size="14" maxlength="14" onblur="formataCPF(this, event, null)" onkeypress="return formataCPF(this, event, null)" onfocus="getEl('radioCpf').dom.checked = true;">
						<f:converter converterId="convertCpf"/>
          				<f:param name="type" value="cpf" />						
					</h:inputText>
				</td>
			</tr>
			<tr>
				<td width="1px;" style="text-align:left"> <input type="radio" name="tipoBusca" value="nome" class="noborder" id="radioNome" ${ buscaPessoa.tipoBusca == "nome" ? "checked='checked'" : "" }/> </td>
				<th width="5%;"> Nome:</th>
				<td><h:inputText value="#{buscaPessoa.obj.nome}" size="60" onfocus="getEl('radioNome').dom.checked = true;"/> </td>
			</tr>
		</tbody>
		<tfoot>
			<tr>
				<td colspan="3">
					<h:commandButton action="#{buscaPessoa.buscar}" value="Buscar"/>
					<h:commandButton action="#{buscaPessoa.cancelar}" value="Cancelar" onclick="#{confirm}"/>
				</td>
			</tr>
		</tfoot>
	</table>
	</h:form>

	<c:if test="${not empty buscaPessoa.resultadosBusca}">
		<br />
		<table class="listagem">
			<caption class="listagem">Selecione uma pessoa da lista abaixo:</caption>
			<thead>
				<tr>
					<td width="10%"><p align="center">CPF</p></td>
					<td>Nome</td>
					<td></td>
				</tr>
			</thead>
			<h:form>
			<c:forEach items="#{buscaPessoa.resultadosBusca}" var="item" varStatus="status">
				<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar"}">
					<td><p align="center"><ufrn:format type="cpf_cnpj" valor="${item.cpf_cnpj }" /></p></td>
					<td>${item.nome}</td>
					<td width="3%">

						<h:inputHidden value="#{buscaPessoa.codigoOperacao}"/>
						<h:commandLink action="#{buscaPessoa.escolhePessoa}"
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