<%@include file="/WEB-INF/jsp/include/cabecalho.jsp"%>

<style type="text/css">
ul.descricaoOperacao li{
   list-style-type: square;
} 
</style>

<h2>  <ufrn:subSistema /> &gt; Visualizar Papéis do Módulo de Bibliotecas do ${configSistema['siglaSigaa']}</h2>

	<div class="descricaoOperacao"> 
     	<p> Nesta página é possível visualizar os papéis do sistema de bibliotecas com suas respectivas operações. </p>
     	<p> O usuário que possuir um determinado papel no sistema poderá realizar as operações que o papel possui. </p>
     	<br/>
     	<p> <strong>Observação 1:</strong> As atribuições dos papeis no sistema são de competência dos GESTORES DE PERMISSÃO e são realizadas 
     	no <strong>${configSistema['siglaSigadmin']}</strong>. </p>
     	<br/>
     	<p> <strong>Observação 2:</strong> Para usuários que necessitam realizar uma certa operação no sistema apenas durante um período limitado de tempo, é 
     	possível atribuir papeis temporários. Neste caso, depois da data determinada, o usuário perderá automaticamente o papel. </p>
	</div>

	<table class="listagem" style="width: 100%;">		
		
		<caption> Papéis e suas Operações no Sistema </caption>

		<thead>
			<th style="text-align: left; width: 40%;">Papel</th>
			<th style="text-align: left; width: 60%;">Operações</th>
		</thead>

		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_ADMINISTRADOR_GERAL </strong> <br/>
				<em> Realiza todas as operações do sistema de biblioteca</em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao"> <li> TODAS AS OPERAÇÕES DO SISTEMA </li> </ul> <br/>
			</td>
		</tr>

		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong>BIBLIOTECA_ADMINISTRADOR_LOCAL </strong> <br/>
				<em> Realiza algumas configurações e operações específica para uma biblioteca do sistema.</em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Lista e Alterar configurações da sua biblioteca <i>[Aba Cadastros]</i> </li> <br/>
					<li> Cadastrar e Remover uma Interrupção para SUA biblioteca <i>[Aba Circulação]</i> </li> <br/>
					<li> Gerenciar os Inventários dos Acervo para a SUA biblioteca <i>[Aba Cadastros]</i> </li> <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CATALOGACAO_BIBLIOTECARIO </strong> <br/>
				<em> Realiza todas as operações do setor de processos técnicos </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao"> 
					<li> Todas as Operações do Setor Processos Técnicos </li>  <br/>
					<li> Cadastrar, Alterar e Remover um Novo Campo MARC Local <i>[Aba Cadastros]</i> </li>  <br/>
					<li> Cadastrar, Alterar e Remover uma Nova Planilha Bibliográfica ou de Autoridades <i>[Aba Cadastros]</i> </li>  <br/>
					<li> Registrar, Alterar e Remover Chegada de Fascículos (Fascículos de Doação) <i>[Aba Aquisições]</i> </li>  <br/>
					<li> Atender uma solicitação de Normalização e Catalogação na Fonte <i>[Aba Informação e Referência]</i> </li>  <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CATALOGACAO_GERENCIAR_MATERIAIS </strong> <br/>
				<em> Papel que dá permissão aos usuário de incluírem e alterar as informações de materiais do acervo do sistema. Realizam
				praticamente todas as operações do setor de processos técnicos, menos alterar os dados MARC das catalogações. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Incluir exemplares tombados  </li>  <br/>
					<li> Incluir Fascículos  </li>  <br/>
					<li> Alterar as informações de Exemplares e Fascículos  </li>  <br/>
					<li> Alterar dados de Vários Materiais ao mesmo Tempo.  </li>  <br/>
					<li> Incluir Notas de Circulação  </li>  <br/>
					<li> Exportar um Título  </li>  <br/>
					<li> Exportar um Autoridade </li>  <br/>
					<li> Imprimir Etiquetas </li>  <br/>
					<li> Baixar, Remover, Desfazer Baixa e Substituir Exemplares e Fascículos </li>  <br/>
					<li> Pesquisas no Acervo </li>  <br/>
					<li> Pesquisas na base de Autoridades </li>  <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CATALOGACAO </strong> <br/>
				<em> Realiza as tarefas dentro de processos técnicos que não alterem os dados do acervo. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Exportar Título  </li>  <br/>
					<li> Exportar Autoridade </li>  <br/>
					<li> Impressão de Etiquetas </li>  <br/>
					<li> Pesquisas no Acervo </li>  <br/>
					<li> Pesquisas na base de Autoridades </li>  <br/>
				</ul>
			</td>
		</tr>


		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CATALOGACAO_SEM_TOMBAMENTO </strong> <br/>
				<em> Permite incluir exemplares não tombados no acervo. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Incluir Exemplares não tombados no acervo  </li>  <br/>
				</ul>
			</td>
		</tr>



   
   		<tr class="linhaPar">
			<th style="text-align: left;"> <strong>BIBLIOTECA_SETOR_CIRCULACAO_BIBLIOTECARIO </strong> <br/>
				<em> Realiza todas as operações do setor de circulação. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Todas as Operações do Setor de Circulação </li>  <br/>
					<li> Realizar Empréstimos, Renovaçãoes e Devoluções <i>[Módulo de Circulação Desktop]</i> </li>  <br/>
					<li> Autorizar um Usuário Desfazer uma Operações <i>[Módulo de Circulação Desktop]</i> </li>  <br/>
				</ul>
			</td>
		</tr>
		
		
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CIRCULACAO </strong> <br/>
				<em> Realiza as tarefas dentro de processos técnicos que não alterem os dados do acervo. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Realizar Empréstimos, Renovaçãoes e Devoluções <i>[Módulo de Circulação Desktop]</i> </li>  <br/>
					<li> Bloquear e Desbloquear um Usuários </li> <br/>
					<li> Comunicar Material Perdido  </li> <br/>
					<li> Consultar os Materiais que estão Emprestados para um Usuário  </li> <br/>
					<li> Consultar o Histórico de Empréstimos de um Usuário  </li> <br/>
					<li> Consultar o Histórico de Empréstimos de um Material  </li> <br/>
					<li> Incluir e Remover Notas de Circulaçãoa  </li> <br/>
					<li> Confirmar Pagamento de Multas Manualmente  </li> <br/>
					<li> Imprimir e Reimprimir GRUs para pagamento de Multas   </li> <br/>
					<li> Visualizar Reservas de um Usuário.   </li> <br/>
					<li> Solicitar Reservas para um Usuário.   </li> <br/>
					<li> Visualizar Reservas de um Título do acervo. </li> <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CIRCULACAO_CHECKOUT  </strong> <br/>
				<em> Realiza exclusivamente a operação de "ckeckout" no módulo de circulação </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Realizar o Checkout  <i>[Módulo de Circulação Desktop]</i> </li>  <br/>
				</ul>
			</td>
		</tr>
   
   		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_EMITE_DECLARACAO_QUITACAO  </strong> <br/>
				<em> Permite a um usuário que não tenha nenhum papel de circulação verificar a situação de usuários da biblioteca 
				  e emitir o comprovante de quitação. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Verificar Situação dos Usuários e Emitir Declaração de Quitação. </li>  <br/>
				</ul>
			</td>
		</tr>
   
   		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_AQUISICAO_BIBLIOTECARIO  </strong> <br/>
				<em> Permite a realização de todas as operações dentro do setor de aquisição </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Todas as Operações do Setor de Aquisição </li>  <br/>
				</ul>
			</td>
		</tr>
		
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_AQUISICAO  </strong> <br/>
				<em> Realiza operações menos críticas de aquisição. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Registrar Chegada de Fascículos  </li>  <br/>
					<li> Alterar / Remover Fascículos Registrados  </li>  <br/>
					<li> Cadastrar, Alterar e Remover Periodicidades  </li>  <br/>
				</ul>
			</td>
		</tr>
   
   		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_INFO_E_REF_BIBLIOTECARIO  </strong> <br/>
				<em> Realiza todas as operações de informação e referência. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Todas as Operações do Setor de Informação e Referência </li>  <br/>
				</ul>
			</td>
		</tr>
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_INFO_E_REF  </strong> <br/>
				<em> Realiza operações menos críticas de Informação e Referência. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Realizar, Renovar e Devolver Empréstimos Institucionais</li>  <br/>
				</ul>
			</td>
		</tr>
  
  
  		<tr class="linhaPar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_SETOR_CONTROLE_ESTATISTICO  </strong> <br/>
				<em> Permite ao usuário que o possua visualizar  os relatórios do sistema. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Emitir Relatórios de Processos Técnicos e Circulação   </li>  <br/>
					
				</ul>
			</td>
		</tr>
		
		<tr class="linhaImpar">
			<th style="text-align: left;"> <strong> BIBLIOTECA_GESTOR_BDTD </strong> <br/>
				<em> Realiza as operações relacionadas a BDTD no sistema. </em> 
			</th>
			<td style="text-align: left;">
				<ul class="descricaoOperacao">
					<li> Catalogar uma Teses ou Dissertações   </li>  <br/>
					<li> Consultar Teses ou Dissertações Pendentes de Publicação na BDTD    </li>  <br/>
					<li> Consultar Solicitações (TEDE)   </li>  <br/>
					<li> Consultar Bancas    </li>  <br/>
				</ul>
			</td>
		</tr>
		
		<tfoot>
			<tr>
				<td colspan="2" style="height: 20px;">
				</td>
			</tr>
		</tfoot>
   
	</table>


<%@include file="/WEB-INF/jsp/include/rodape.jsp"%>