<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>
<f:view>

	<h2><ufrn:subSistema /> > Dados Pessoais</h2>
		
		<table class="visualizacao" style="width: 100%">
		
		    <caption> Dados Pessoais </caption>

				<tr>
					<th width="20%">Nome</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.nome}" />
					</td>
				</tr>

				<tr>
					<th width="20%">Identidade</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.pessoa.identidade}" />
					</td>
				</tr>

				<tr>
					<th width="20%">CPF</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.pessoa.cpf_cnpj}" />
					</td>
				</tr>

				<tr>
					<th width="20%">Logradouro</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.pessoa.enderecoContato.logradouro}" />
					</td>
				</tr>

				<tr>
					<th width="20%">Bairro</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.pessoa.enderecoContato.bairro}" />
					</td>
				</tr>

				<tr>
					<th width="20%">CEP</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.pessoa.enderecoContato.cep}" />
					</td>
				</tr>

				<tr>
					<th width="20%">Telefone</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.pessoa.telefone}" />
					</td>
				</tr>

				<tr>
					<th width="20%">Sexo</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.pessoa.sexo}" />
					</td>
				</tr>

				<tr>
					<th width="20%">Nascimento</th>
					<td><h:outputText value="#{servidor.servidorSelecionado.pessoa.dataNascimento}" />
					</td>
				</tr>

		<table class="formulario" style="width: 100%">
			<tfoot>
				<tr>
					<td>
						<input type=button value="Voltar para lista" onClick="history.go(-1)">
					</td>
				</tr>
			</tfoot>
		</table>

	</table>	
	
</f:view>
<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>
