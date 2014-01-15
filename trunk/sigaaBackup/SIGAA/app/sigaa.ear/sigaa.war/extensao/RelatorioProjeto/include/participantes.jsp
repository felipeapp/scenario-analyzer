<table class="formulario" style="width: 100%">
	
		<tr>
			<td colspan="2" class="subFormulario"> Lista de Participantes do Projeto </td>
		</tr>	
		
		<tr>
			<td colspan="2">
				<table class="listagem">
						<thead>
							<tr>
								<th style="text-align: center;">Nº</th>
								<th style="text-align: center;">CPF</th>
								<th>Nome</th>
								<th>Participação</th>								
								<th style="text-align: center;">Certificado</th>
								<th></th>
							</tr>
						</thead>
			
						<tbody>
							<c:forEach items="#{relatorioAcaoExtensao.mbean.obj.atividade.participantes}" var="participante" varStatus="status">
								<tr class="${status.index % 2 == 0 ? "linhaPar" : "linhaImpar" }">
									<td style="text-align: center;">${status.count}</td>
									<td style="text-align: center;"> <ufrn:format valor="${participante.cadastroParticipante.cpf}" type="cpf"/>  </td>
									<td>${participante.cadastroParticipante.nome} </td>
									<td>${participante.tipoParticipacao.descricao}</td>			
									<td style="text-align: center;">${participante.autorizacaoCertificado ? 'SIM' : 'NÃO'}</td>
								</tr>
							</c:forEach>
							
							<c:if test="${empty relatorioAcaoExtensao.mbean.obj.atividade.participantes}">
								<tr><td colspan="6"><center><i>Não há participantes cadastrados</i></center></td></tr>
							</c:if>
											
						</tbody>
				</table>			
			</td>
		</tr>
		
</table>