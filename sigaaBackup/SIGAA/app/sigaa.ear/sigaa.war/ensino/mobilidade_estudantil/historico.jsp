<table class="listagem" style="width: 100%">
	<caption>Histórico de Movimentações do Discente</caption>
	<thead>
		<tr>
			<td style="width: 100px; text-align: center;">Ano-Período</td>
			<td>Tipo</td>
			<td>Instituição</td>
			<td>Usuário</td>
			<td style="text-align: center;">Data</td>
			<td style="text-align: center;">Hora</td>
			<td>Situação</td>
			<td style="width: 5%;"></td>
			<td style="width: 5%;"></td>
		</tr>
	</thead>
	<tbody>
	<c:if test="${empty mobilidadeEstudantil.historicoMovimentacoes}">
		<tr>
			<td colspan="9" style="text-align: center;">
				<i>Nenhuma Mobilidade Estudantil Cadastrada.</i>					
			</td>				
		</tr>
	</c:if>				
	<c:if test="${not empty mobilidadeEstudantil.historicoMovimentacoes}">
		<c:forEach items="#{mobilidadeEstudantil.historicoMovimentacoes}" var="mov" varStatus="status">
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td style="text-align: center;">${mov.ano}.${mov.periodo} à ${mov.anoPeriodoFim}</td>
				<c:if test="${mov.interna}">
					<td>Interna</td>	
					<td>${mov.campusDestino.nome}</td>			
				</c:if>
				<c:if test="${!mov.interna}">
					<td>Externa</td>
					<td>${mov.iesExterna}</td>				
				</c:if>				
				<td>${mov.registroCadastro.usuario.pessoa.nome}</td>
				<td style="text-align: center;"><ufrn:format type="data" valor="${mov.dataCadastro}" /></td>
				<td style="text-align: center;"><ufrn:format type="hora" valor="${mov.dataCadastro}" /></td>
				<td>
					<c:if test="${mov.ativo}">
						Ativo
					</c:if>
					<c:if test="${not mov.ativo}">
						<span style="color:red;">Cancelada</span>
					</c:if>							
				</td>	
				<td>
					<c:if test="${mov.ativo && not mobilidadeEstudantil.cadastro}">
						<h:commandButton id="btAlterar" image="/img/alterar.gif" title="Alterar Mobilidade Estudantil"
							action="#{mobilidadeEstudantil.alterarMobilidade}" styleClass="noborder">
							<f:setPropertyActionListener value="#{mov}" target="#{mobilidadeEstudantil.obj}"/>
						</h:commandButton>					
					</c:if>
				</td>
				<td>
					<c:if test="${mov.ativo && not mobilidadeEstudantil.cadastro}">
						<h:commandButton id="btCancelar" image="/img/graduacao/cancelar16.png" onclick="return confirm('Ao Cancelar esta Mobilidade, o aluno ficará sem vínculo com a instituição. Deseja Continuar?');" title="Cancelar Mobilidade Estudantil"
							action="#{mobilidadeEstudantil.cancelarMobilidade}" styleClass="noborder">
							<f:setPropertyActionListener value="#{mov}" target="#{mobilidadeEstudantil.obj}"/>
						</h:commandButton>			
					</c:if>									
				</td>				
			</tr>
			<tr class="${status.index % 2 == 0 ? 'linhaPar' : 'linhaImpar'}">
				<td colspan="9">
					<b>Observação:</b><br/>
					<p>${mov.observacao}</p>
				</td>
			</tr>
		</c:forEach>
	</c:if>		
	</tbody>
</table>