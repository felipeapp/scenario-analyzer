<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Interesse em Participar</h2>

<div id="ajuda" class="descricaoOperacao">
	<p>
		<center><strong>Informar Interesse em Participar</strong></center>
	</p>
	<p>
		Este espaço é uma forma de facilitar a comunicação entre o aluno e o professor responsável pela bolsa. 
		Aqui você deverá informar todas as suas qualidades relevantes para a seleção para que o professor tenha acesso na hora que estiver procurando candidatos.
	</p>
	<p>
		O professor receberá os dados informados aqui, por isso é muito importante que você informe todos os dados corretamente, tanto de qualificações como os dados de contato para depois o professor poder entrar em contato com você. 
	</p>	
	<p>
		Vale ressaltar que o preechimento e submissão deste formulário não lhe garante o direito de uma bolsa. Apenas registra o seu interesse em participar, pois é responsabilidade do prefessor a atribuição das bolsas. 
	</p>
	<hr/>
	<p>
		<center><a href="http://lattes.cnpq.br/" target="_blank">Plataforma Lattes</a></center>
	</p>
	<p>
		Através da Plataforma Lattes os professores conseguirão extrair mais informações sobre você. As Bolsas de Pesquisa, por exemplo, so irão aceitar alunos que possuam o endereço para o curriculo lattes.
	</p>
	<p>
		Nesta etapa você não é obrigado a informar seu currículo lattes, mas se desejar fazer mais tarde vá até: Portal do Discente -> Atualizar Foto e Perfil ou <a href="/sigaa/portais/discente/perfil.jsf">clique aqui.</a>
	</p>
	<br/>
	<p>
		<a href="http://lattes.cnpq.br/conteudo/aplataforma.htm" target="_blank" id="detalhesLatters">Mais detalhes sobre o Lattes, clique aqui.</a>
	</p>
</div>

<f:view>

<br />
<h:form id="listagemResultado">
		
		<table class="visualizacao">
			<tbody>
				<c:if test="${agregadorBolsas.colunas.descricao}">
					<tr>
						<th>Título:</th>
						<td>${ interessadoBolsa.agregadorBolsa.descricao }</td>
					</tr>
				</c:if>
				
				<c:if test="${agregadorBolsas.colunas.vagasRemuneradas}">
					<tr>
						<th>Vagas Remuneradas:</th>
						<td>${ interessadoBolsa.agregadorBolsa.vagasRemuneradas }</td>
					</tr>
				</c:if>		
				
				<c:if test="${agregadorBolsas.colunas.vagasNaoRemuneradas}">
					<tr>
						<th>Vagas Voluntárias:</th>
						<td>${ interessadoBolsa.agregadorBolsa.vagasNaoRemuneradas }</td>
					</tr>
				</c:if>		
				
				<c:if test="${agregadorBolsas.colunas.responsavelProjeto}">
					<tr>
						<th>Responsável:</th>
						<td>${ interessadoBolsa.agregadorBolsa.responsavelProjeto.pessoa.nome }</td>
					</tr>
				</c:if>		
				
				<c:if test="${agregadorBolsas.colunas.unidade}">
					<tr>
						<th>Unidade:</th>
						<td>${ interessadoBolsa.agregadorBolsa.unidade }</td>
					</tr>
				</c:if>			
				
				<c:if test="${agregadorBolsas.colunas.tipoBolsa}">
					<tr>
						<th>Tipo Bolsa:</th>
						<td>${ interessadoBolsa.agregadorBolsa.tipoBolsa }</td>
					</tr>
				</c:if>		
				
				<c:if test="${agregadorBolsas.colunas.bolsaValor}">
					<tr>
						<th>Remuneração:</th>
						<td>${ interessadoBolsa.agregadorBolsa.bolsaValor }</td>
					</tr>
				</c:if>	
				
				<c:if test="${agregadorBolsas.colunas.emailContato}">
					<tr>
						<th>Email:</th>
						<td>${ interessadoBolsa.agregadorBolsa.emailContato }</td>
					</tr>
				</c:if>		
							
				<c:if test="${agregadorBolsas.colunas.cursosAlvo}">
					<tr>
						<th>Interesse nos cursos:</th>
						<td>${ interessadoBolsa.agregadorBolsa.cursosAlvo }</td>
					</tr>
				</c:if>								
			</tbody>
		</table>
	</h:form>

<br />

	<h:form id="form">
		<table class="formulario" width="100%">
			<caption>Inscrição em Oportunidade</caption>
			<tbody>
				<tr>
					<td>
						<b>Descrição Pessoal:</b><span class="obrigatorio" />
					</td>
				</tr>
				<tr>
					<td align="center">
						<t:inputTextarea id="descricaoPessoal" value="#{interessadoBolsa.perfil.descricao}" rows="5" style="width: 95%"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>Áreas de Interesse:</b><span class="obrigatorio" />
					</td>
				</tr>
				<tr>
					<td align="center"><t:inputTextarea id="areasInteresse" value="#{interessadoBolsa.perfil.areas}"style="width: 95%"/></td>
				</tr>
				<tr>
					<td><b>Currículo Lattes: </b></td>
				</tr>
				<tr>
					<td align="center">
						<t:inputText id="curriculoLattes" value="#{interessadoBolsa.perfil.enderecoLattes}"style="width: 96%"/>
					</td>
				</tr>	

				<tr>
					<td class="subFormulario">Qualificação</td>
				</tr>
				<tr>
					<td style="padding: 5px 15px; text-align: center; font-style: italic;">
						Descreva suas qualificações, experiências ou qualquer outro atributo relevante ao processo seletivo
					</td>
				</tr>
				<tr>
					<td>
						<b>Qualificações:</b><span class="obrigatorio" />
					</td>
				</tr>												
				<tr>
					<td> 
						<h:inputTextarea id="qualificacoes" value="#{interessadoBolsa.obj.dados.qualificacoes}" style="width: 95%; margin: 3px 10px;" rows="8" /> 
					</td>
				</tr>								
			</tbody>
			
			<tfoot>
				<tr>
					<td colspan="2">
						<h:commandButton value="Inscrever-se" action="#{interessadoBolsa.finalizarInscricaoInteresse}" id="inscreverse"/>
						<h:commandButton value="Cancelar" onclick="#{confirm}"	action="#{agregadorBolsas.iniciarBuscar}" id="cancelOperation"/>
					</td>
				</tr>			
			</tfoot>
		</table>
	
	<center>
		<br />	
		<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigatório. </span>		
	</center>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>