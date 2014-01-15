<table class="visualizacao" style="width: 90%">
	<caption>Dados da Oferta de Estágio</caption>
	<tr>
		<td colspan="4" class="subFormulario">Dados do Concedente do Estágio</td>
	</tr>
	<tr>
		<th style="width: 30%;">Tipo do Convênio:</th>
		<td colspan="3">
			<h:outputText value="#{oferta.concedente.convenioEstagio.tipoConvenio.descricao}"/>
		</td>
	</tr>	
	<tr>
		<th>CNPJ:</th>
		<td colspan="3">
			<h:outputText value="#{oferta.concedente.pessoa.cpfCnpjFormatado}"/>
		</td>
	</tr>
	<tr>
		<th>Nome:</th>
		<td colspan="3">
			<h:outputText value="#{oferta.concedente.pessoa.nome}"/>																																				
		</td>
	</tr>
	<tr>
		<th>Responsável:</th>
		<td colspan="3">
			<h:outputText value="#{oferta.concedente.responsavel.pessoa.nome}"/>
		</td>
	</tr>	
	<tr>
		<td colspan="4" class="subFormulario">Dados da Oferta de Estágio</td>
	</tr>
	<tr>
		<th style="width: 30%;">Título:</th>
		<td colspan="3">
			<h:outputText value="#{oferta.titulo}"/>
		</td>
	</tr>
	<tr>
		<th>Número de Vagas:</th>
		<td colspan="3">
			<h:outputText value="#{oferta.numeroVagas}"/> 				
		</td>
	</tr>
	<tr>
		<th style="width: 15%;">Valor da Bolsa:</th>		
		<td>
			<ufrn:format type="moeda" valor="${oferta.valorBolsa}"></ufrn:format>
		</td>
		<th>Aux. Transporte:</th>
		<td>
			<ufrn:format type="moeda" valor="${oferta.valorAuxTransporte}"></ufrn:format> ao dia
		</td>	
	</tr>
	<tr>
		<th>Início da Publicação:</th>
		<td>
			<ufrn:format type="data" valor="${oferta.dataInicioPublicacao}"></ufrn:format>
		</td>
		<th>Fim da Publicação:</th>
		<td>
			<ufrn:format type="data" valor="${oferta.dataFimPublicacao}"></ufrn:format> 							
		</td>
	</tr>			
	
	<tr>
		<td class="subFormulario" colspan="4">Cursos para os quais as vagas serão Ofertadas</td>			
	</tr>	
	
	<tr>
		<td colspan="4">
			<table class="listagem" style="width: 100%">
				<thead>
				<tr>
					<th style="text-align: left;">Cursos</th>				
				</tr>
				</thead>			
				<c:forEach items="#{oferta.cursosOfertados}" var="_curso">
					<tr><td style="text-align: left;">${_curso.nomeCompleto}</td></tr>
				</c:forEach>
			</table>
		</td>			
	</tr>		
	
	<tr>
		<td class="subFormulario" colspan="4">Descrição detalhada da Oferta</td>			
	</tr>	 
	
	<tr>
		<td colspan="4" style="text-align: justify;">
		   ${oferta.descricao}				
		</td>
	</tr>			
</table>	