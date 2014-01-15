<table class="visualizacao" style="width: 90%;">
	<caption>Dados do Est�gio</caption>
	<tr>
		<td colspan="4" class="subFormulario">Dados do Discente</td>
	</tr>		
	<tr>
		<th>Discente:</th>
		<td colspan="3">${estagio.discente.matricula} - ${estagio.discente.nome}</td>
	</tr>
	<tr>
		<th>Curso:</th>
		<td colspan="3">${estagio.discente.curso.descricao}</td>
	</tr>
	<c:if test="${estagio.discente.pessoa.tipoNecessidadeEspecial != null}">
		<tr>
			<th>Necessidade Especial:</th>
			<td colspan="3">${estagio.discente.pessoa.tipoNecessidadeEspecial.descricao}</td>
		</tr>		
	</c:if>		
	<tr>
		<td colspan="4" class="subFormulario">Dados do Concedente</td>
	</tr>		
	<tr>
		<th>Concedente:</th>
		<td colspan="3">
			<h:outputText value="#{estagio.concedente.pessoa.nome}"/>																																				
		</td>
	</tr>
	<tr>
		<th>Respons�vel:</th>
		<td colspan="3">
			<h:outputText value="#{estagio.concedente.responsavel.pessoa.nome}"/>
		</td>
	</tr>
	<tr>
		<th>Supervisor:</th>
		<td colspan="3">
			<h:outputText value="#{estagio.supervisor.nome}"/>																																				
		</td>
	</tr>					
	<tr>
		<td colspan="4" class="subFormulario">Dados Gerais do Est�gio</td>
	</tr>									
	<tr>
		<th>Tipo do Est�gio:</th>
		<td colspan="3">
			${estagio.tipoEstagio.descricao}
		</td>
	</tr>
	<tr>
		<th>Descri��o das Atividades:</th>
		<td colspan="3">
			<h:outputText value="#{estagio.descricaoAtividades}"/>
		</td>
	</tr>				
	<tr>
		<th style="width: 200px;">Carga Hor�ria Semanal:</th>
		<td style="width: 150px;">
			<h:outputText value="#{estagio.cargaHorariaSemanal}"/>
		</td>									
		<th style="width: 200px;">Alterna Teoria e Pr�tica? :</th>
		<td>
			<ufrn:format type="simnao" valor="${estagio.alternaTeoriaPratica}"></ufrn:format>
		</td>
	</tr>	
	<tr>
		<th>Professor  Orientador do Est�gio:</th>
		<td colspan="3">
			${estagio.orientador.pessoa.nome}				
		</td>
	</tr>					
	<tr>
		<th>In�cio do Est�gio:</th>
		<td>
			<ufrn:format type="data" valor="${estagio.dataInicio}"></ufrn:format>
		</td>
		<th>Fim do Est�gio:</th>
		<td>
			<ufrn:format type="data" valor="${estagio.dataFim}"></ufrn:format>			
		</td>
	</tr>	
	<tr>
		<th>Hora de In�cio:</th>
		<td>
			<ufrn:format type="hora" valor="${estagio.horaInicio}"></ufrn:format>
		</td>
		<th>Hora de T�rmino:</th>
		<td>
			<ufrn:format type="hora" valor="${estagio.horaFim}"></ufrn:format>			
		</td>
	</tr>				
	<tr>
		<th>Valor da Bolsa:</th>		
		<td>
			<ufrn:format type="valor" valor="${estagio.valorBolsa}"></ufrn:format>
		</td>
		<th>Valor Aux. Transporte:</th>		
		<td>
			<ufrn:format type="valor" valor="${estagio.valorAuxTransporte}"></ufrn:format> ao dia
		</td>			
	</tr>
	<tr>
		<td colspan="4" class="subFormulario">Dados do Seguro contra Acidentes Pessoais</td>
	</tr>	
	<tr>
		<th>CNPJ:</th>
		<td colspan="3">
			<h:outputText value="#{estagio.cnpjSeguradoraFormatado}"/>
		</td>
	</tr>
	<tr>
		<th>Seguradora:</th>
		<td colspan="3">				
			<c:if test="${not empty estagio.seguradora}">
				${estagio.seguradora}				
			</c:if>
			<c:if test="${empty estagio.seguradora}">
				N�o informado.
			</c:if>
		</td>
	</tr>	
	<tr>
		<th>Ap�lice do Seguro:</th>		
		<td>
			<c:if test="${not empty estagio.apoliceSeguro}">
				${estagio.apoliceSeguro}				
			</c:if>
			<c:if test="${empty estagio.apoliceSeguro}">
				N�o informado.
			</c:if>
		</td>
		<th>Valor Seguro:</th>		
		<td>
			<ufrn:format type="valor" valor="${estagio.valorSeguro}"></ufrn:format>
		</td>			
	</tr>
</table>