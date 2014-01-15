<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<h2><ufrn:subSistema /> > Interesse em Participar</h2>

<div id="ajuda" class="descricaoOperacao">
	<p>
		<center><strong>Informar Interesse em Participar</strong></center>
	</p>
	<p>
		Este espa�o � uma forma de facilitar a comunica��o entre o aluno e o professor respons�vel pela bolsa. 
		Aqui voc� dever� informar todas as suas qualidades relevantes para a sele��o para que o professor tenha acesso na hora que estiver procurando candidatos.
	</p>
	<p>
		O professor receber� os dados informados aqui, por isso � muito importante que voc� informe todos os dados corretamente, tanto de qualifica��es como os dados de contato para depois o professor poder entrar em contato com voc�. 
	</p>	
	<p>
		Vale ressaltar que o preechimento e submiss�o deste formul�rio n�o lhe garante o direito de uma bolsa. Apenas registra o seu interesse em participar, pois � responsabilidade do prefessor a atribui��o das bolsas. 
	</p>
	<hr/>
	<p>
		<center><a href="http://lattes.cnpq.br/" target="_blank">Plataforma Lattes</a></center>
	</p>
	<p>
		Atrav�s da Plataforma Lattes os professores conseguir�o extrair mais informa��es sobre voc�. As Bolsas de Pesquisa, por exemplo, so ir�o aceitar alunos que possuam o endere�o para o curriculo lattes.
	</p>
	<p>
		Nesta etapa voc� n�o � obrigado a informar seu curr�culo lattes, mas se desejar fazer mais tarde v� at�: Portal do Discente -> Atualizar Foto e Perfil ou <a href="/sigaa/portais/discente/perfil.jsf">clique aqui.</a>
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
						<th>T�tulo:</th>
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
						<th>Vagas Volunt�rias:</th>
						<td>${ interessadoBolsa.agregadorBolsa.vagasNaoRemuneradas }</td>
					</tr>
				</c:if>		
				
				<c:if test="${agregadorBolsas.colunas.responsavelProjeto}">
					<tr>
						<th>Respons�vel:</th>
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
						<th>Remunera��o:</th>
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
			<caption>Inscri��o em Oportunidade</caption>
			<tbody>
				<tr>
					<td>
						<b>Descri��o Pessoal:</b><span class="obrigatorio" />
					</td>
				</tr>
				<tr>
					<td align="center">
						<t:inputTextarea id="descricaoPessoal" value="#{interessadoBolsa.perfil.descricao}" rows="5" style="width: 95%"/>
					</td>
				</tr>
				<tr>
					<td>
						<b>�reas de Interesse:</b><span class="obrigatorio" />
					</td>
				</tr>
				<tr>
					<td align="center"><t:inputTextarea id="areasInteresse" value="#{interessadoBolsa.perfil.areas}"style="width: 95%"/></td>
				</tr>
				<tr>
					<td><b>Curr�culo Lattes: </b></td>
				</tr>
				<tr>
					<td align="center">
						<t:inputText id="curriculoLattes" value="#{interessadoBolsa.perfil.enderecoLattes}"style="width: 96%"/>
					</td>
				</tr>	

				<tr>
					<td class="subFormulario">Qualifica��o</td>
				</tr>
				<tr>
					<td style="padding: 5px 15px; text-align: center; font-style: italic;">
						Descreva suas qualifica��es, experi�ncias ou qualquer outro atributo relevante ao processo seletivo
					</td>
				</tr>
				<tr>
					<td>
						<b>Qualifica��es:</b><span class="obrigatorio" />
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
		<html:img page="/img/required.gif" style="vertical-align: top;"/> <span class="fontePequena"> Campos de preenchimento obrigat�rio. </span>		
	</center>
	
	</h:form>
</f:view>

<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>