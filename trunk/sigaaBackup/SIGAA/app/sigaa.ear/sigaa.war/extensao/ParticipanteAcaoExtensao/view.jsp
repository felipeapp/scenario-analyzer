<%@include file="/WEB-INF/jsp/include/cabecalho_impressao.jsp"%>

<f:view>
	
	<h2>Dados do Participante da Ação de Extensão</h2>

		<table class="tabelaRelatorio" width="100%" >
			<caption class="listagem">Dados do Participante</caption>

			<tr>
				<th width="20%">Ação de Extensão:</th>
				<td><h:outputText value="#{participanteAcaoExtensao.obj.acaoExtensao.codigoTitulo}" id="acao"/></td>
			</tr>

			<tr>
				<th width="20%">Tipo de Ação:</th>
				<td><h:outputText value="#{participanteAcaoExtensao.obj.acaoExtensao.tipoAtividadeExtensao.descricao}" id="tipo_acao"/></td>
			</tr>


			<tr>
				<th>Nome:</th>
				<td>
					<h:outputText value="#{participanteAcaoExtensao.obj.nome}" id="nome"/>
				</td>
			</tr>

			<c:if test="${participanteAcaoExtensao.obj.passaporte == null}">
				<tr>
					<th>CPF:</th>
					<td>
						<h:outputText value="#{participanteAcaoExtensao.obj.cpf}" id="cpf"/>
					</td>
				</tr>
			</c:if>
			<c:if test="${participanteAcaoExtensao.obj.passaporte != null}">
				<tr>
					<th>Passaporte:</th>
					<td>
						<h:outputText value="#{participanteAcaoExtensao.obj.passaporte}" id="passaporte"/>
					</td>
				</tr>
			</c:if>


			<tr>
				<th>Data Nascimento:</th>
				<td>
					<h:outputText value="#{participanteAcaoExtensao.obj.servidor.pessoa.dataNascimento}" id="dataNascimentoServidor"/>
					<h:outputText value="#{participanteAcaoExtensao.obj.discente.pessoa.dataNascimento}" id="dataNascimentoDiscente" rendered="#{participanteAcaoExtensao.obj.servidor.pessoa.dataNascimento==null}"/> 
					<h:outputText value="#{participanteAcaoExtensao.obj.dataNascimento}" id="dataNascimentoOutros" rendered="#{participanteAcaoExtensao.obj.servidor.pessoa.dataNascimento==null && participanteAcaoExtensao.obj.discente.pessoa.dataNascimento==null}"/>
				</td>
			</tr>

			<tr>
				<th>E-mail:</th>
				<td>
					<h:outputText value="#{participanteAcaoExtensao.obj.email}" id="email"/>
				</td>
			</tr>

			<tr>
				<th>Instituição:</th>
				<td>
				<h:outputText value="#{participanteAcaoExtensao.obj.instituicao}" id="instituicao"/>
				<h:outputText value="UFRN" rendered="#{empty participanteAcaoExtensao.obj.instituicao}"/>
				</td>
			</tr>

			<tr>
				<th>Endereço:</th>
				<td>
				<h:outputText value="#{participanteAcaoExtensao.obj.endereco}" id="endereco"/>
					${participanteAcaoExtensao.obj.discente.pessoa.enderecoContato.logradouro} 
					${participanteAcaoExtensao.obj.discente.pessoa.enderecoContato.numero}
					${participanteAcaoExtensao.obj.discente.pessoa.enderecoContato.bairro}
					
					${participanteAcaoExtensao.obj.servidor.pessoa.enderecoContato.logradouro}
					${participanteAcaoExtensao.obj.servidor.pessoa.enderecoContato.numero}
					${participanteAcaoExtensao.obj.servidor.pessoa.enderecoContato.bairro}
				</td>
			</tr>

			<tr>
				<th>CEP:</th>
				<td>
				<h:outputText value="#{participanteAcaoExtensao.obj.cep}" id="cep"/>
				</td>
			</tr>


			<tr>
				<th>Tipo de Participante:</th>
				<td>
					<h:outputText id="tipoParticipante"	value="#{participanteAcaoExtensao.obj.tipoParticipanteString}"	/>
				</td>
			</tr>


			<tr>
				<th>Participação como:</th>
				<td>
					<h:outputText id="tipoParticipacao"	value="#{participanteAcaoExtensao.obj.tipoParticipacao.descricao}"	/>
				</td>
			</tr>


			<tr>
				<th>Frequência:</th>
				<td>
					<h:outputText id="frequencia"	value="#{participanteAcaoExtensao.obj.frequencia}"/>%
				</td>
			</tr>

			<tr>
				<th>Receber Certificado?</th>
				<td>
					<h:outputText id="certificado"	value="#{participanteAcaoExtensao.obj.autorizacaoCertificado ? 'SIM':'NÃO'}"/>
				</td>
			</tr>

			<tr>
				<th>Receber Declaração?</th>
				<td>
					<h:outputText id="declaracao"	value="#{participanteAcaoExtensao.obj.autorizacaoDeclaracao ? 'SIM':'NÃO'}"/>
				</td>
			</tr>

		</table>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape_impressao.jsp"%>