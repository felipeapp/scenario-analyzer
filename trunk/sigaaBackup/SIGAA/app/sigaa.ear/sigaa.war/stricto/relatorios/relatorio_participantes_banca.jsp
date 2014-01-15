<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp" %>



<f:view>

<h2>Relatório de Participantes das Bancas</h2>

<c:forEach items="#{ participantesDasBancas.bancas }" var="programa">
	<p><strong>Programa:</strong> <h:outputText value="#{programa.key}"/> <br/></p>
	<p><strong>Ano/Período:</strong> <h:outputText value="#{ participantesDasBancas.anoBanca }.#{ participantesDasBancas.periodoBanca }"/></p>
	
	<br/>
	<div id="parametrosRelatorio">
	<c:forEach items="#{programa.value}" var="banca">
		<br/>
		<table class="tabelaRelatorioBorda" style="width: 100%">
			<caption>
				<h:outputText value="#{banca.dadosDefesa.discente.matricula} - #{banca.dadosDefesa.discente.nome} 
					(#{banca.tipoDescricao} #{banca.dadosDefesa.discente.nivelDesc}) -"/> 
				<h:outputText value="#{banca.data}" />
			</caption>
			<thead>
				<tr>
					<th style='width: 23%;text-align: right'>CPF ou Passaporte</th>
					<th style='width: 31%'>Nome</th>
					<th style='width: 15%'>Email</th>
					<th style='width: 10%'>Instituição</th>
					<th style='width: 20%'>Formação</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="#{banca.membrosBanca}" var="membro" varStatus="i">
					<tr>
						<td style='text-align: right'>
							<h:outputText value="#{membro.pessoa.cpf_cnpj}" rendered="#{not empty membro.pessoa.cpf_cnpj}"/>
							<h:outputText value="#{membro.pessoa.passaporte} (#{membro.pessoa.pais.nome })"  
								rendered="#{not empty membro.pessoa.passaporte && empty membro.pessoa.cpf_cnpj && not empty membro.pessoa.pais}"/>
						</td>	
						<td><h:outputText value="#{ membro.nome }"/></td>
						<td><h:outputText value="#{ membro.email }"/></td>
						<td><h:outputText value="#{ membro.instituicao.sigla }"/></td>
						<td><h:outputText value="#{ membro.maiorFormacao.denominacao }"/></td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</c:forEach>
	</div>
</c:forEach>

</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp" %>