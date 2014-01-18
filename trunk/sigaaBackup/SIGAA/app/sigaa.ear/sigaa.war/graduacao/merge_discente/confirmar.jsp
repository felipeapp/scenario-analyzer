<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<f:view>
	<h2><ufrn:subSistema /> > Unificar Dados Pessoais do Discente</h2>
	<div class="descricaoOperacao">
		<p><b>Caro Usuário</b></p>
		<p>Este formulário permite unificar os dados pessoais de dois cadastros de discentes e deve ser realizado com muita atenção.</p>
		<p>Ambos discentes ficarão somente com os dados pessoais em comum escolhido pelo usuário, as demais informações relativas ao discente ficam inalteradas.</p> 
	</div>
	<br />
		
		<h:form id="form">
		<table class="listagem" style="width:100%;">
			<caption> Dados Pessoais ao Qual Todos Discentes Serão Associados</caption>
			<c:forEach items="#{mergeDadosDiscenteMBean.discentes}" var="discente" varStatus="status">
				<c:if test="${ discente.selecionado }" >
				<tr>
					<td rowspan="5" width="50px" style="text-align: center;"> 
						<c:if test="${discente.idFoto != null}">
							<img src="${ctx}/verFoto?idFoto=${discente.idFoto}&key=${ sf:generateArquivoKey(discente.idFoto) }" width="50" height="63"/>
						</c:if>
						<c:if test="${discente.idFoto == null}">
							<img src="${ctx}/img/no_picture.png" width="50" height="63"/>
						</c:if>
					</td>
				</tr>
				<tr>
					<th width="12%" style="text-align:right; vertical-align: top" class="rotulo"> CPF: </th>
					<td colspan="3"> ${discente.pessoa.cpfCnpjFormatado} </td>
				</tr>
				<tr>
					<th width="12%" style="text-align:right; vertical-align: top" class="rotulo"> Nome: </th>
					<td colspan="3"> ${discente.pessoa.nome} </td>
				</tr>
				<tr>
					<th style="text-align:right; vertical-align: top" class="rotulo"> Identidade: </th>
					<td> ${discente.pessoa.identidade} </td>
					<th style="text-align:right vertical-align: top" class="rotulo"> Data Nasc.: </th>
					<td> ${ discente.pessoa.celular} </td>
				</tr>
				<tr>
					<th width="12%" class="rotulo">Nome da Mãe:</th>
					<td> ${discente.pessoa.nomeMae} </td>
					<th width="12%" class="rotulo">Nome do Pai:</th>
					<td> ${discente.pessoa.nomePai} </td>
				</tr>
			</c:if>
			</c:forEach>
		</table>
		<br/>
		<table class="listagem" style="width:100%;">
			<caption> Discentes que Serão Unificados</caption>
			<thead>
				<tr>
					<th width="10%" style="text-align: center;"> Matrícula </th>
					<th width="35%"> Nome </th>
					<th> Curso </th>
					<th width="12%"> Status </th>
				</tr>
			</thead>
			<tbody>
			<c:forEach items="#{mergeDadosDiscenteMBean.discentes}" var="discente" varStatus="status">
				<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
					<td width="9%" style="text-align: center;">${discente.matricula}</td>
					<td>${discente.nome}</td>
					<td>${ discente.curso.descricaoCompleta } (${ discente.nivelDesc })</td>
					<td width="8%">${discente.statusString}</td>
				</tr>
			</c:forEach>
			<tr>
				<td colspan="4">
					<c:set var="exibirApenasSenha" value="true" scope="request"/>
					<div style="text-align: center; width: 100%" >
						<%@include file="/WEB-INF/jsp/include/confirma_senha.jsp"%>
					</div>
				</td>
			</tr>
			</tbody>
			<tfoot>
				<tr>
					<td colspan="4" style="text-align: center;ss">
					<h:commandButton value="Unificar Discentes" action="#{mergeDadosDiscenteMBean.cadastrar}" id="unificar"/>
					<h:commandButton value="Cancelar" onclick="#{confirm}" action="#{mergeDadosDiscenteMBean.cancelar}" id="cancelar"/>
					</td>
				</tr>
			</tfoot>
		</table>
	</h:form>
	<br>
	<center><html:img page="/img/required.gif" style="vertical-align: top;" /> <span
		class="fontePequena"> Campos de preenchimento obrigatório. </span> <br>
	<br>
	</center>
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
